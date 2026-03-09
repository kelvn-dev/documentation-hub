
## Find file

find [directory-path] [options] [search-term]
```
find . -iname "filename.txt"
find . -name "*.txt"
```

## Search text inside files

grep [options] pattern [file...]

```
grep -c -i java interview-preparation/java.md
```

-i (ignore case)	Perform a case-insensitive search
-n (line number)	Display matching lines with their line numbers
-v (invert match)	Print lines that do not match the pattern
-c (count)	Display the count of matching lines, not the lines themselves
-w (whole word)	Match the whole word only, not as a substring
-r (recursive)	Search for the pattern recursively in all files within a directory and its subdirectories
Piping (|)	Filter the output of another command.	ps -ef | grep "apache"

## Archive

```
tar -xvf archive.tar.gz -C /dir
```

## Process Management

```
ps -ef
```

Real-time process monitoring
```
top
```

## Check port & kill process

```
lsof -i :8080
```

force fill
```
kill -9 <pid>
```

## Curl

```
curl -X POST -H "Content-Type: application/json" -d '{"name":"Kelvin"}' http://localhost:8080/api
```

## chmod

u: user who created file
g: users who are members of the file's group
o: other users
a: u+g+o

r: read
w: write
x: execute

operators: + to add, - to remove, = to set exactly permission

symbolic mode
```
chmod u+x file.md
chmod -R u+x temp
```

numeric mode use 3 digits, each digit corresponds to the owner, group, and others and here is number corresponding for symbol:
r: 4
w: 2
x: 1
```
chmod 750 filename  # Owner has full access (rwx), group has r+x, other has no permission
```