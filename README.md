# Profiling Metabase running in a separate JVM

This is a tiny Clojure script that connects [clj-async-profiler](https://clojure-goes-fast.com/kb/profiling/clj-async-profiler/)
to Metabase running in its own JVM, and captures a (sampling) profile of what that JVM is doing.

## Usage

### Step 1 - Launch Metabase

Start Metabase with eg.

```
$ MB_JETTY_PORT=3333 java -Djdk.attach.allowAttachSelf \
  -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints \
  -jar metabase.jar
```

plus whatever other environment variables you need (eg.`MB_DB_CONNECTION_URI`).

Those flags aren't essential, if you're having issues with any of them.

### Step 2 - Get the PID

```
$ ps -ef | grep "jar metabase"
501 12345 789 ...
```

### Step 3 - Run the profiler

From this repo, using the PID from the above step.

```
$ clj -X:connect :port 8787 :pid 12345
{:port 8787 :pid 12345}
Starting 5000ms profile of 12345...
Profile complete. Serving UI on 8787 and waiting for you to quit.
[clj-async-profiler.ui] Started server at /127.0.0.1:8787
```

You can pass any options available on `clj-async-profiler.core/profile` on the
command line - see the [docs](https://clojure-goes-fast.com/kb/profiling/clj-async-profiler/basic-usage/#functions-and-options).

You can also pass the special option `:duration 30000` to control the duration of the profiler run.
Don't be afraid to set it to a minute or two in typical configurations; it doesn't log so much that it will produce a huge file or run out of memory.

### Step 4 - Do things in Metabase

While the profiler is running during Step 3, click stuff in Metabase to
trigger the actions you're trying to profile. Refresh a query or dashboard, etc.

### Step 5 - Analysis

Visit `localhost:8787` (or whatever port you chose). Note that the files are saved to
a temporary directory, and persist betweens runs of the tool.

Be careful to check the timestamps of which profile you're looking at; the numbering
resets at `1` each time the server is restarted, which is a bit of a mess with this
tool that keeps restarting!

There's good [docs](https://clojure-goes-fast.com/kb/profiling/clj-async-profiler/) about
how to analyze the flamegraphs produced by this tool.

Note that you can profile allocations, break out the profile by thread, see profiles
for all blocked threads as well as the default running threads, and much more. See the 
async profiler docs.
