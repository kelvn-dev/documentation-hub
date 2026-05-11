# Database architecture

MVCC, Query optimizer, Lock, ...

## Backup and restore data

- Before any changes are applied to the main datafile on disk, they are first recorded in an append-only log called the WAL (event not commit yet) => Write to wal sequentially
- Apply log: After db is up again, it will search for wal and update until the latest status
- Undo: Use rollback information to undo uncommited data

## Scanning strategy

### Sequential scan

Full table scan, reads every row in table

It's used when there is no index or the cost of reading index and random io exceed the cost of sequential scan. 

Good for small tables or queries return a large percentage of the table (e.g., > 20%)

### Index scan

Index scan include 2 main steps, finding row pointers from index then fetching the actual row data from the table heap

It's used when there is suitable index

Good for high selectivity and bad for low selectivity due to high random io

Bonus: Db perform index-only scan when all needed data already located in index and no need to fetch from table heap

### Bitmap scan

Bitmap scan combine the speed of index with the efficiency of sequential table access. After quickly find all index entries, db scan all pointers to construct a "bitmap" in memory that represent the physical locations of matching rows, then table heap is scanned using sequential io based on the bitmap to retrieve rows. These 2 steps are called bitmap index scan and bitmap heap scan

In postgre, bitmap scan use exact mode to remember specific rows when there is enough memory. If exceed memory, switch to lossy mode to remember only pages and have to recheck all rows on a page (explain show Recheck Cond). Config work_mem for this

It's used to avoid high cost of random io while still avoid full table scan (5 - 20% of table)

## Index

Index is just a data structure that improves read performance by finding specific rows without scanning the entire table but need more disk storage and slower write performance. It cost disk storage separately from the actual table data and store a copy of specified column values in order with pointers to the corresponding rows in the main table, for example ctid in postgre or rowid in oracle

- B+ Tree Index (default): use balanced tree structure that support equality, range query and ORDER BY
- Hash Index: use hash table that support equality but not range query
- Unique Index: ensures no duplicate values
- Composite Index: index on multiple columns to optimize query filtering on multiple fields
- Bitmap Index: for low-cardinality columns like status or region
- Partial Index: Index only a subset of rows to reduces index size and improves performance
  (CREATE INDEX idx_active_users ON users(email) WHERE active = true;)

query will not use index if condition break SARGability
- UPPER(), LOWER(), ...
- created_time::date = abc
- amount * 2 = abc => amount = abc / 2
- like "%abc%" => like "abc%" (range scan)

Solution when unable to rewrite query is to create expression index, but query have to use the same function. Can only create expression index with deterministic function, which mean input and output are always similar

Reducing disk io by including select column into index to make it index-only scan.

For example
CREATE INDEX idx_emp_cover
  ON employees (subsidiary_id, UPPER(last_name))
  INCLUDE (first_name, last_name, salary);

Including key only appear at leaf node, and read-only, cannot access or sort

### B+tree

Although binary search tree cost only log₂n, index dont use it because each data node is stored randomly in separate locations on disk, so each node lookup requires a slow disk I/O operation and each time database read data on disk, it read a page, each page cost several kilo bytes while each data node is only several bytes, so we are wasting 99% data for each page

Main idea of B+tree is that each node contain full data of a page, all entries within a node are sorted and each entry contain a copy of row value from index column with pointers to the corresponding rows in the main table, for example ctid in postgre or rowid in oracle. Nodes are linked by doubly linkedlist to support both range query without extra disk io operation and bidirectional order asc, desc without the need to create 2 seperate index 

B+ tree is a self-balancing search tree designed to minimize disk I/O by keeping the tree short and wide. Structure:
- Root node: starting point of the tree and it's almost always cached in ram. It can be a leaf node or an internal node if the tree is small
- Internal node: nodes store only keys, not actual data to route the search process to the correct leaf node
- Leaf node: nodes at the bottom-most level and contain the actual data like pointer. Every key in the tree is represented at this level. Nodes are linked with doubly linked list

a node contain n keys will have n+1 sub node
b+ tree store all actual data entries in leaf nodes, root and internal node only store seperator key to route search key to correct leaf node
each level = 1 disk read
1 leaf node = 1 page, entries are sorted so can perform binary search in ram after load from disk 

### Index lookup

3 steps:
- Tree traversal: each level = 1 disk read
- Leaf Node Chain: from first leaf node, db traverse to next leaf node as they are connected by doubly linked list, to get all matching entries. For unique index, it finish immediately
- Table access: each matching entry contain pointer, but row stored randomly on disk, so db have to perform random disk io for each entry if select other columns
  
Query optimizer can estimate number of found rows based on statistics to skip index and use full table scan for sequential io if there are many matching rows (low selectivity)

### Composite index

Composite index follow leftmost prefix rule, so if we declare index(A, B, C), we have to query by A, A and B, A and B and C, db cannot use index if query skip leading column like B, B and C. If we query by A and C, only A is access predicate, while C is just index filter predicate.

Access predicate is condition that B-tree can traverse to correct position to limit the search range, while index filter predicate is just filter condition applied for each index entry from access predicate, so cannot limit the search range. Table filter predicate is the most expensive, it filter rows fetched from table heap

If we query equality by A and range by B and equality by C, only A and B are access predicate, while C is index filter predicate because range query make all subsequent columns in composite index only used for index filter predicate

Order matter in index definition but not in query, the query parser can reorder condition

When design composite index
- List all possible query patterns with frequency
- Put equality column first, range after and prioritize column with most frequency to reuse index as most as possible. If there are multiple range, choose the most selective for access predicate
- Check side effects

### Clustered vs non-clustered index

Both use B+tree but leaf node in clustered index store actual row data while leaf node in non-clustered index only store row pointer. Clustered index also determines the physical order of the table because rows are stored in the order of the clustered column. There is only 1 clustered index per table because data can only be stored in one physical order.

Databases that support clustered index like mysql or sql server default create clustered index for primary key, while postgre and oracle use unique b+tree

## Join

When join more than 2 tables like A x B x C, db actually perform multiple join between 2 tables like AxB, then ABxC. Optimizer may reorder like AxC or BxC to choose the optimized plan. Setting join_collapse_limit specify max number of tables in a join query that optimizer can reorder, default value is 8. Set join_collapse_limit=1 to keep join order in query

There are 3 main algorithms to join

### Nested loops

Similar to brute force, scan all rows from inner table for each row from outer table. when A x B x C, it doesn't store result of A x B into ram, Db use pipeline, meaning every row from A x B immediately move to next step, just like java stream api

Create index on joining column in inner table so that each inner lookup is just a B-tree traversal instead of full scan. Cost down from M x N to M x tree height

Nested loops is not good when number of rows from driving table is large

### Hash join

Work like java HashMap, scan driving table, each id go through hash function to put in a bucket, then scan each row from inner table, each foreign key go through the same hash function to go to the same bucket. If there is any collision like multiple row inside same bucket, traverse all to compare id directly. Cost down from M x N to M + N

Optimizer switch to use hash join when outer table is large, no index on joining column and built hash table fit with the allocated ram which is limited by work_mem

When hash table exceed ram, switch to hybrid hash join, hash table is split to multiple batch, only batch 0 put in ram, other batch go into disk, whenever a batch is finish, it's released and next batch load from disk into ram to process

### Merge join

When 2 table already have joining column sorted like a B-tree index, merge join algorithm efficiently maintains a pointer for each table, compares the current rows from both and keep increasing the pointer until end. Cost is M + N, need less ram and the cost of sorting is free if query have ORDER BY joining column but worst if cannot skip the sort phase which take NlogN

DB switch to use merge join algorithm when 2 datasets are large and already sorted on the join key because it can skip the sort phase.

## Execution plan

Enterprise systems like orcale or sql server have global execution plan cache, so when any user executes a query, the engine hashes the query text and checks if a plan already exists in the global cache to resue, which reduces CPU cost for query accross multiple sessions

While postgre or mysql have to replan every query from scratch. To reuse a plan, we must use a Prepared Statement, but it's only work within the same session

shared buffer is a region of RAM to cache data blocks from tables (config shared_buffers), while shared read is number of blocks have to read from OS cache or disk

### Plan caching

Custom plan is generated fresh for every execution, while generic plan is created once and reused across multiple executions regardless of the parameter values. For custom plan, db must re-plan the query every time it runs but it always use the optimized scanning strategy for provided parameter values, so it's good if the optimal strategy depends heavily on the parameter values

By default, postgre use custom plans for the first 5 executions. On the 6th execution, it generates a generic plan and compares with average cost of the 5 custom plans to decide if generic plan will be cached and reused for all future executions in that session. That's why the same query but executed later may get slower due to generic plan 

Config behavior using plan_cache_mode:
- auto (default)
- force_custom_plan
- force_generic_plan

Check current plan_cache_mode: EXPLAIN (GENERIC_PLAN)

Solution when generic plan is choosen but not good:
- force_custom_plan
- Reset 5-execution rule by DEALLOCATE and re-PREPARE

### Red flag

- Large gap between estimated return rows and actual return rows. Fix: run ANALYZE table_name to update statistics for query optimizer
- High loops. Actual time is just average time for each loop, so we have to multiple actual time with loops to get full time

## Lock

### Index lock

The nature of index is just a place to save list value of that column in asc order (default), so when we update a record, db need to lock the index to rebuild it

![](images/index-lock.png)

### Escalating lock

If there is a table with lots of row (100m for example), user execute an update statement that will lock 99% of this table, this cost a lot because db also need resource to know which object is locked, so it will lock the whole table instead of 99%
=> Each db has different rule for escalating lock but Oracle do not escalating lock

### Dead lock

Happen when 2 transactions waiting for the other one to release the lock

![](images/dead-lock1.png)

When dead lock happen, system will kill/abort 1 transaction and it will choose the one that use less resource, because this transaction cost less to rollback

How to avoid:
- If need to update multi tables, try to do it in the same order accross all transactions 
    ![](images/avoid-dead-lock1.png)

- But above solution not completely resolve issue, for example:
    ![](images/dead-lock2.png)

=> If need to update multi record in the same table, try to get all the lock at once, for example "select ... where id = 1 and id = 3 for update" or "update ... where id = 3 and id = 3"

## MVCC (Multi version concurrent control)

Problem: when user A update record A from value 0 -> 1 but not commit yet, then user B query record A should see value 0 and user A query record A should see value 1, how 2 users query the same record at the same time but see two different values.
=> All types of db utilize MVCC to solve this problem, but have different implementation

### MVCC in Postgresql

ctid is a system column that exists in every table and represents the physical location of a row version where data is stored on the disk. Format is a pair of integers, such as (0, 1), where the first number is the block (or page) and the second is the offset within that block

When update record A (ctid A) in table A, postgresql insert another record with updated value to that same table, user A will query the inseted record and other users will query the old record. The inserted record will have different ctid that reference ctid A for rollback

![](images/postgresql-mvcc-implementation.png)

After commit or rollback, 1 of 2 row becomse useless, postgre use vacuum to clear these useless column periodically

This causes:
- Need to rebuild index of all columns
- At a time, number of real rows is just some millions but number of rows in table can be more than 2 or 3 times
- Process to clear unused rows happen inside real table, which will affect performance

### MVCC in Mysql

Mysql have a space called undo, when update, it update directly to record and save old value in undo space. User A  see value of record in table, other users see value in undo space

![](images/mysql-mvcc-implementation.png)

This help:
- Only need to rebuild index of updated column
- Number of real rows and rows in table is always the same
- Process to clear unused record in undo space, not tables, which will not affect performace

## Execution steps for query

When user execute a query statement, there are 6 steps:
- Check syntax
- Check context (Ex: if queried column exist in table or if user has permission to access that column)
=> Above 2 steps not affect performace
- Check execution plan in cache
- If execution plan not found in cache, it need to analyze and choose the execution plan that cost least (exetremely complex)
=> This is the factor to evaluate db's performance (Ex: Postgresql can rewrite the whole subquery, while mysql just analyze it normally), which make a db expensive or not
- Create the choosen execution plan
- Execute it

=> This why when we update db's version, same query in same context can be faster, just because the query optimizer to analyze execution plan has been improved

## Strategies for scaling

For read-heavy system like chatgpt, utilize master-slave and config the master run in high-availability mode with a hot standby that is always ready to take over serving traffic if the primary down. 

To avoid situation where low-priority workload becomes resource-intensive that can affect performance of high-priority requests on the same instances, split requests into low-priority and high-priority tiers and route them to separate instances. The implementation can be at application-level by using use a routing DataSource to dynamically select the correct replica

Implement cache locking mechanism so that when multiple requests miss on the same cache key, only one request retrieve data and rebuild the cache, all other requests wait for the cache

HikariCP is application-level pool, which manage connections per application instance, for example 10 pods × 20 connections = 200 DB connections, so scaling pods increases DB connections linearly.

Utilize PgBouncer as a proxy layer (sits between application and PostgreSQL) to pool database connections centrally, for example 10 pods can share 50 real DB connections. Much better for high scalable system but add a network layer, while hikari is extremely fast because in-memory.

Best practice is using both. HikariCP manages thread inside each instance, while PgBouncer protects database

## Anti-Patterns

### Long-Lived transactions

Better patterns:
- In app code, use short transactions (open as late as possible, close as early as possible) and avoid doing network calls like HTTP inside a database transaction
- Make sure connection pools have idle_in_transaction_session_timeout set

### Hotspot rows

One table. One row. Everyone updates it, for example using a row as counter

```
UPDATE metrics SET pageviews = pageviews + 1 WHERE id = 1;
```

Better patterns:
- append-only by using `insert` and aggregate later

## Normalization

Normalization is the process of organizing data to reduce redundancy. For example, table order shouldn't store customer info because it repeat many times and when customer update contact, we have to update many rows, instead seperate into customer table and use foreign key for reference