# ASSIGNMENTS

## Assignment #01 - Poool Game

The game consists in a bidimensional board with a number of small balls and two bigger balls, representing a human player ball and a bot (i.e. computer controlled) ball.

The number of small balls can be high (thousands). All balls can move and bounce, against the border or each other. We consider elastic collisions and friction force, so that a moving ball stops after a while. At the top of the board, in the corners, there are two circles representing holes. The objective of the game for the players (human and bot) is to kick the small balls in the holes, by throwing their own balls in a sequence of throws.

### Setup

The `assignment-01` folder in the repo includes:
* `src` folder, which includes the game in two different versions:
  1. `thread_version` based on Java multithreaded programming, using only default threads
  2. `task_version` applying Task-based approach, using Java Executor Framework
* `doc` folder with a short report in pdf (`report.pdf`)



## Assignment #02 - FSStat

The assignment is about implementing a library called `FSStatLib` providing an asynchronous method called `getFSReport` and a simple program exemplifying the use of the library. The asynchronous method getFSReport (parameters: D, MaxFS, NB) must compute some statistics about the size of the files of some directory D, including recursively its subdirectories. In particular, the method should asynchronously compute and return a report R including:
- The total number of files that belongs to D (including subdirectories, recursively);
- The distribution of file sizes, that is: given a file size MaxFS and a number of file size bands NB dividing the file size range [0, MaxFS], the method computes for each band the number of files with a size included in it and the number of files with a size bigger that MaxFS (so complexively NB + 1 size ranges).

The library and the example program must be developed producing three different versions:
1. Asynchronous programming based on event-loops
2. Reactive programming using Rx
3. Virtual Threads

### Setup

The `assignment-02` folder in the repo includes:
* `src` folder, which includes the program in three different versions
  1. `eventLoop` based on Java framework Vert.x
  2. `reactiveRx` applying Reactive Programming (RxJava)
  3. `virtualThreads` version
* `doc` folder with a short report in PDF (`report.pdf`)



## Assignment #03

## Assignment #04
