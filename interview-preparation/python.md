# Python

## How does Python execute a program

Python compiles the source code into bytecode, .pyc files and usually stored in __pycache__ to skip recompilation, then the Python Virtual Machine such as CPython executes the bytecode

## Data type

Immutable:

int
float
string
bool
tuple

Mutable:

list
set
dict

## args vs kwargs

Both used to pass a dynamic number of arguments to a function, but args is position-based arguments that contain a tuple, while kwargs is name-based arguments that contain a dict

can use both but order must be *args after regular parameters and before **kwargs

```
def my_function(title, *args, **kwargs):
    print("Title:", title)
    print("Positional arguments:", args)
    print("Keyword arguments:", kwargs)

my_function("User Info", "Emil", "Tobias", age=25, city="Oslo")
# Output:
# Title: User Info
# Positional arguments: ('Emil', 'Tobias')
# Keyword arguments: {'age': 25, 'city': 'Oslo'}
```

## lambda

Lambda functions are commonly used with built-in functions like map(), filter(), and sorted()

```
numbers = [1, 2, 3, 4, 5, 6]

even_numbers = list(filter(lambda x: x % 2 == 0, numbers))
doubled = list(map(lambda x: x * 2, numbers))

students = [("Emil", 25), ("Tobias", 22), ("Linus", 28)]
sorted_students = sorted(students, key=lambda x: x[1])
```

## Closure

Closure is inner function that can access or modify varaible if we mark it as nonlocal from surrounding scope

```
def fibonacci():
  x0 = 0
  x1 = 1
  def get_next_number():
    nonlocal x0, x1
    x0, x1 = x1, x0 + x1
    return x1
  return get_next_number

fib = fibonacci()
for i in range(1, 5):
  number = fib()
  print(f'The {i}th Fibonacci number is: {number}')
```

## Decorator

Decorator is wrapper of a function to modify behavior without changing existing code. For ex, instead of putting the logging code directly in a service function, we create a decorator to make the code for this concern reusable and not disrupt business logic

```
from functools import wraps

def my_decorator(func):
    @wraps(func)  # <--- wraps ensures metadata (__name__, __doc__) preservation
    def wrapper(*args, **kwargs):
        """Wrapper docstring."""
        print("Before function")
        func(*args, **kwargs)
        print("After function")
    return wrapper

@my_decorator
def my_function():
    """Original docstring."""
    print("Hello!")

print(my_function.__name__)  # Output: say_hello
print(my_function.__doc__)   # Output: Original docstring.
```

if multiple decorator applied, the order will be start from bottom to top

## OOP

No access modifier, instead use _var for protected and __var for private, but this is just naming convention, there is no enforcement

```
from abc import ABC, abstractmethod

# abstraction
class Vehicle(ABC):
    @abstractmethod
    def start_engine(self):
        pass

# inheritance
class Car(Vehicle):
    def __init__(self, brand, model):
        self.brand = brand
        self.__model = model

    # polymorphism
    def start_engine(self):
      ...

    # encapsulation
    def get_model(self):
        return self.__model
```

## yield vs return

Both return value but 'return' terminates the function while 'yield' pauses the execution of the function and returns the value, when next() is called again, it continues from the yield statement paused last time. So yield is kind of lazyload to handle large dataset more efficient, for example load line by line instead of load entire file into memory

```
def read_lines(file_path):
    with open(file_path, "r") as f:
        for line in f:
            yield line

for line in read_lines("big_file.txt"):
    print(line)
```

## Context manager

Context manager is used to ensure cleanup of resources even if errors occur during execution. It's kind of a more concise version of try finally 

```
with open("my_file.txt", "r") as f:
    content = f.read()
```

## Magic method

Magic methods are special methods start and end with double underscores that allow Python objects to interact with built-in operations like operators, iteration, ... For example when we call len(object), internally object.__len__() is called

__init__, __eq__, __hash__, ...

### __getattribute__ vs __getattr__

__getattribute__ is called for every attribute access, while __getattr__ is only called when the attribute does not exist

```
class Test:
    def __getattribute__(self, name):
        print("getattribute called")
        return super().__getattribute__(name)
    def __getattr__(self, name):
        print("getattr called")
        return "default"

t = Test()
t.x

# __getattribute__ runs first, then __getattr__ runs
```

If implement __getattribute__, must call the parent implementation, Otherwise cause infinite recursion because accessing self.name again calls __getattribute__

### __new__ vs __init__

__new__ runs before __init__ because Python first needs to create the object in memory. __new__ allocates and returns the instance, and then __init__ set attributes

### __str__ vs __repr__

Both show string representation but purpose of __str__ is for user friendly, while __repr__ is for developer debugging

## Multithreading

corresponding module for thread and process: threading, multiprocessing

GIL (Global interpreter lock) ensure only one thread executes Python bytecode at a time in a single process. So for cpu task, use multiprocessing to bypass the limit of gil.

multithreading still useful despite the gil because many operations release the gil during blocking I/O

Semaphore limits number of threads accessing a resource

## == vs is
== compare value, 'is' compare object reference, for example:
```
a = [1,2]
b = [1,2]

a == b   # True
a is b   # False
```

