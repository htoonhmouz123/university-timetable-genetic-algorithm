# University Course Timetable Scheduler — Genetic Algorithm

A Java / JavaFX desktop application that automatically generates a university course timetable using a **Genetic Algorithm (GA) implemented from scratch**, with a **Hill Climbing** baseline for comparison.

Built as the individual course project for **Artificial Intelligence (COMP338)** at Birzeit University.

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-GUI-007396?style=flat&logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=flat)

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [How It Works](#how-it-works)
- [Getting Started](#getting-started)
- [Input File Format](#input-file-format)
- [Experiments and Results](#experiments-and-results)
- [Project Structure](#project-structure)
- [Limitations and Future Work](#limitations-and-future-work)
- [Author](#author)
- [License](#license)

---

## Overview

Building a semester timetable means assigning every course a day, a time slot, and a room, while respecting many rules at once: no instructor or room can be double-booked, rooms must be big enough and of the right type, and students in the same group must not have overlapping classes.

With 6 rooms, 5 days and 4 periods per day, each course has **120 possible placements**. A schedule of 20 courses therefore has about 120²⁰ possible timetables, which is far too many to check one by one. A genetic algorithm searches this space by evolving a population of candidate timetables toward ones with no conflicts and as few undesirable patterns as possible.

---

## Features

- **Genetic Algorithm built from scratch** (no external GA library): chromosome encoding, random initial population, fitness evaluation, selection, crossover, mutation, elitism, and three termination conditions.
- **6 hard constraints and 5 soft constraints**, combined into one weighted fitness function.
- **Interchangeable operators**, selectable from the GUI:
  - Selection: Tournament (default) or Roulette Wheel
  - Crossover: Two-point (default) or One-point
  - Mutation: Random reassignment (default) or Swap
- **Hill Climbing** baseline, with a one-click GA vs. Hill Climbing convergence chart.
- **JavaFX GUI**: load a dataset from a file or enter one manually, tune every GA parameter, view the final timetable in a table, and watch the fitness convergence chart.
- **Export** the timetable (`.txt`), the fitness history (`.csv`), and the dataset (`.txt`).
- **Built-in experiment runner** that sweeps population size, mutation rate, crossover rate, and operator choices, and measures behaviour as the problem grows, writing results to CSV.
- **Five ready-made datasets** (10 to 50 courses) plus a 30-course demo dataset.

---

## Screenshots

> Add your own screenshots to `docs/screenshots/` using these file names, or delete this section.

| Main window with timetable | Fitness convergence | GA vs. Hill Climbing |
| :---: | :---: | :---: |
| ![Timetable](docs/screenshots/timetable.png) | ![Convergence](docs/screenshots/convergence.png) | ![Comparison](docs/screenshots/comparison.png) |

---

## How It Works

### Representation

A **chromosome** is one complete timetable. It holds one **gene per course**, in a fixed course order. A gene stores its course and a single integer, the **slot index**, which encodes a (day, time, room) combination:

```
slotIndex = timeSlotIndex × numberOfRooms + roomIndex
```

With 20 time slots and 6 rooms, slot indices run from 0 to 119. The `SlotTable` class converts between a slot index and its time slot and room. Storing one integer per gene keeps crossover and mutation simple.

### Fitness function

```
Penalty = 100 × (hard violations) + 10 × (soft violations)
Fitness = 1 / (1 + Penalty)
```

Fitness is between 0 and 1, and a perfect timetable has fitness 1. The large hard-constraint weight makes the GA remove conflicts before it optimises preferences.

### Constraints

**Hard constraints** (must hold for a valid timetable):

| ID | Rule |
| --- | --- |
| H1 | An instructor cannot teach two courses at the same time |
| H2 | A room cannot host two courses at the same time |
| H3 | Courses that share a student group cannot overlap |
| H4 | Room capacity must be at least the number of students |
| H5 | Lab courses need lab rooms; lecture courses need lecture rooms |
| H6 | A course cannot be scheduled when its instructor is unavailable |

**Soft constraints** (quality preferences):

| ID | Rule |
| --- | --- |
| S1 | Avoid the first period of the day (08:00) |
| S2 | Avoid the last period of the day (11:00) |
| S3 | Minimise empty periods (gaps) between a student group's classes |
| S4 | Respect instructors who prefer morning classes |
| S5 | Avoid spreading a student group's classes over more than 4 days |

### Algorithm

```
Generate random initial population
repeat each generation:
    evaluate fitness of every chromosome
    keep the best chromosomes unchanged (elitism)
    fill the rest of the new population by:
        select two parents (tournament or roulette)
        crossover with probability = crossover rate
        mutate each child gene with probability = mutation rate
until a termination condition is met
```

**Termination conditions**

- **A.** A timetable has 0 hard violations and an acceptable number of soft violations. The GUI default threshold is 5; the experiment runner uses 0.
- **B.** The maximum number of generations is reached (default 500).
- **C.** The best fitness has not improved for 50 consecutive generations.

**Default configuration**

| Parameter | Value |
| --- | --- |
| Population size | 100 |
| Maximum generations | 500 |
| Crossover rate | 0.80 (two-point) |
| Mutation rate | 0.05 (random reassignment) |
| Elitism | 5% of the population (at least 1) |
| Selection | Tournament (size 4) |

---

## Getting Started

### Requirements

- JDK 17 or newer
- [JavaFX SDK](https://gluonhq.com/products/javafx/) 17 or newer, downloaded and unzipped

> The project uses the default Java package and has no build file, so it can be opened directly in an IDE or compiled from the command line.

### Run from the command line

Replace `/path/to/javafx-sdk/lib` with the `lib` folder of your JavaFX SDK.

```bash
# compile
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -d out src/*.java

# run
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -cp out App
```

### Run from IntelliJ IDEA

1. Open the project folder and mark `src` as the **Sources Root**.
2. Add the JavaFX SDK's `lib` folder as a library (File > Project Structure > Libraries).
3. In the run configuration for `App`, add these VM options:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls
   ```
4. Run `App`.

### Using the app

1. Click **Load File** and choose one of the datasets in [`data/`](data), for example `data20.txt`. You can also click **Add data** to enter rooms, instructors, courses and groups manually.
2. Adjust the GA parameters if you want, or keep the defaults.
3. Click **Run**. The timetable table, the convergence chart, and a statistics line (violations, penalty, fitness, generations, run time) are shown.
4. Tick **Compare HC & GA** before running to plot Hill Climbing next to the GA.
5. Use **Save File**, **Save Fitness Graph** and **Save DataSet** to export results.
6. **Run Experiments** and **Run Dataset size experiment** run the automated experiment suites and save the results as CSV.

---

## Input File Format

Datasets are plain-text files with four sections. Lines starting with `#` begin a new section.

```
#ROOMS
R101,50,Lecture
LAB1,30,Lab

#INSTRUCTORS
I01,Dr. Ahmad,true,Monday-10:00
I02,Dr. Sara,false,-

#COURSES
C01,AI,35,I01,Lecture
C02,Database,40,I02,Lecture

#GROUPS
G1,C01;C02
```

| Section | Columns |
| --- | --- |
| `#ROOMS` | `id, capacity, type (Lecture or Lab)` |
| `#INSTRUCTORS` | `id, name, prefersMorning (true/false), unavailable slots` |
| `#COURSES` | `id, name, number of students, instructor id, type (Lecture or Lab)` |
| `#GROUPS` | `id, course ids separated by ;` |

Unavailable slots are written as `Day-HH:MM` (for example `Monday-10:00`), separated by `;`, or `-` for none. Valid days are Sunday to Thursday, and valid times are 08:00, 09:00, 10:00 and 11:00.

### Included datasets

| File | Courses | Student groups |
| --- | :---: | :---: |
| `data/data10.txt` | 10 | 3 |
| `data/data20.txt` | 20 | 5 |
| `data/data30.txt` | 30 | 8 |
| `data/data40.txt` | 40 | 10 |
| `data/data50.txt` | 50 | 13 |
| `data/demo-30courses.txt` | 30 | 6 |

The five `dataNN` files share the same 6 rooms and 10 instructors, so larger files make the problem harder rather than just bigger. `demo-30courses.txt` is a separate, roomier dataset (11 rooms, 16 instructors).

---

## Experiments and Results

All experiments below use the 20-course dataset with a fixed random seed (42), so only the parameter under test changes. Each configuration was run once.

### Population size (mutation 5%, crossover 80%)

| Population | Generations | Best fitness | Hard viol. | Soft viol. |
| :---: | :---: | :---: | :---: | :---: |
| 20 | 57 | 0.0196 | 0 | 5 |
| 50 | 50 | 0.0196 | 0 | 5 |
| 100 | 48 | 0.0196 | 0 | 5 |
| 200 | 27 | 0.0196 | 0 | 5 |

Larger populations converged in fewer generations, but all reached the same final quality.

### Mutation rate (population 100, crossover 80%)

| Mutation | Generations | Best fitness | Hard viol. | Soft viol. |
| :---: | :---: | :---: | :---: | :---: |
| 1% | 34 | 0.0243 | 0 | 4 |
| 5% | 27 | 0.0196 | 0 | 5 |
| 10% | 61 | 0.0196 | 0 | 5 |
| 20% | 149 | 0.0071 | 0 | 14 |

Moderate mutation (5% to 10%) worked well. At 20%, too many good assignments were destroyed and the search got much worse.

### Crossover rate (population 100, mutation 5%)

| Crossover | Generations | Best fitness | Hard viol. | Soft viol. |
| :---: | :---: | :---: | :---: | :---: |
| 60% | 191 | 0.0140 | 0 | 7 |
| 80% | 197 | 0.0123 | 0 | 8 |
| 90% | 338 | 0.0163 | 0 | 6 |

### Operator comparisons

| Comparison | Method | Generations | Best fitness | Soft viol. |
| --- | --- | :---: | :---: | :---: |
| Selection | Tournament | 110 | 0.0909 | 1 |
| Selection | Roulette Wheel | 108 | 0.0323 | 3 |
| Crossover | One-point | 35 | 0.0196 | 5 |
| Crossover | Two-point | 29 | 0.0243 | 4 |
| Mutation | Random reassignment | 35 | 0.0196 | 5 |
| Mutation | Swap | 96 | 0.0099 | 10 |

All runs reached 0 hard violations. Tournament selection, two-point crossover and random-reassignment mutation performed best, which is why they are the defaults. Swap mutation can only rearrange slots that are already in use, while random reassignment can reach unused slots among the 120 possible positions.

### Problem size (population 100, mutation 5%, crossover 80%)

| Courses | Generations | Best fitness | Hard viol. | Soft viol. | Run time (ms) |
| :---: | :---: | :---: | :---: | :---: | :---: |
| 10 | 30 | 1.0000 | 0 | 0 | 94 |
| 20 | 46 | 1.0000 | 0 | 0 | 303 |
| 30 | 223 | 0.0323 | 0 | 3 | 750 |
| 40 | 234 | 0.0141 | 0 | 7 | 999 |
| 50 | 376 | 0.0055 | 0 | 18 | 3779 |

With the same 6 rooms and 10 instructors, adding courses makes the problem genuinely harder: soft violations rise from 0 to 18 and run time grows quickly, while the GA still found conflict-free (0 hard violations) timetables at every size.

### Genetic Algorithm vs. Hill Climbing

Hill Climbing starts from one random timetable and repeatedly changes a single course's slot, keeping the change only if fitness improves. In the reported run, the GA stopped as soon as it found an acceptable timetable, while Hill Climbing needed roughly 2,100 iterations to reach a perfect score.

### Example run (default settings, 20 courses)

| Metric | Value |
| --- | --- |
| Courses scheduled | 20 / 20 |
| Hard violations | 0 |
| Soft violations | 4 |
| Final penalty | 40 |
| Best fitness | 0.02439 |
| Generations | 36 |
| Execution time | 59 ms |

> **Note:** these results come from single runs with a fixed seed, so they show trends rather than statistically reliable averages. Running several seeds and averaging would make the comparisons stronger.

---

## Project Structure

```
university-timetable-genetic-algorithm/
├── src/
│   ├── App.java             # Main JavaFX window (entry point)
│   ├── EnterData.java       # Manual data-entry window
│   ├── GA.java              # Genetic algorithm main loop
│   ├── HillClimbing.java    # Baseline search algorithm
│   ├── FitnessCalc.java     # Hard/soft constraint checking and fitness
│   ├── FitnessResult.java   # Result of one fitness evaluation
│   ├── Chromosome.java      # One timetable (list of genes)
│   ├── Gene.java            # One course's slot assignment
│   ├── SlotTable.java       # slotIndex <-> (time slot, room) conversion
│   ├── Selection.java       # Tournament and roulette selection
│   ├── Crossover.java       # One-point and two-point crossover
│   ├── Mutation.java        # Random-reassignment and swap mutation
│   ├── Course.java  Instructor.java  Room.java
│   ├── StudentGroup.java  TimeSlot.java
│   ├── Dataset.java         # Container for all problem data
│   ├── DataLoader.java      # Read/write dataset text files
│   └── ExpRun.java          # Automated experiment runner
├── data/                    # Sample datasets (10-50 courses + demo)
├── docs/screenshots/        # Screenshots used in this README
├── .gitignore
├── LICENSE
└── README.md
```

---

## Limitations and Future Work

- Each course is scheduled for **one weekly session** in a fixed grid of 5 days × 4 periods.
- Results come from a **single seed** per configuration.
- Larger datasets (40 to 50 courses) end with soft violations, so the search could be improved:
  - seed part of the initial population with repaired or heuristic timetables;
  - use an adaptive mutation rate that starts high and decreases;
  - run each experiment over several random seeds and report averages;
  - scale to bigger instances with more instructors and rooms.

---

## Author

**Hatoon Hmouz**
Computer Science student, Birzeit University

- GitHub: [@htoonhmouz123](https://github.com/htoonhmouz123)
- LinkedIn: [hatoon-hmouz](https://www.linkedin.com/in/hatoon-hmouz-28819333a)

---

## License

Released under the [MIT License](LICENSE).
