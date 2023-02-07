#include <iostream>
#include <vector>
#include <list>
#include <thread>
#include <functional>
#include <condition_variable>

using namespace std;

class Workers {
public:
    int n_threads;
    vector<thread> worker_threads;
    list<function<void()>> tasks;
    mutex task_mutex;
    
    bool wait = true;
    mutex wait_mutex;
    condition_variable cv;

    bool run = true;

    Workers () {}
    Workers (int n_threads_) : n_threads(n_threads_) {}

    void start() {
        for (int i = 0; i < n_threads; i++) {
            worker_threads.emplace_back([this, &run = run, &wait = wait, &wait_mutex = wait_mutex, &cv = cv] {
                while (run) {
                    unique_lock<mutex> lock(wait_mutex);
                    while (wait) {
                        cv.wait(lock);
                    }
                    function<void()> task;
                    if (!tasks.empty()) {
                        task = *tasks.begin();
                        tasks.pop_front();
                    }
                    if (task) {
                        task();
                    }
                }
            });
        }
    }   

    void post(const function<void()> &function) {
        {
            unique_lock<mutex> lock(task_mutex);
            tasks.emplace_back(function);
        }
        {
            unique_lock<mutex> lock(wait_mutex);
            wait = false;
        }
        cv.notify_all();
    }

    void join() {
        for (auto &t : worker_threads) {
            t.join();
        }
    }
};

// TODO: Implement stop() and post_timeout()
int main() {
    Workers worker_threads(4);
    Workers event_loop(1);

    worker_threads.start();
    event_loop.start();

    worker_threads.post([] {
        cout << "Task A"
            << " runs in thread "
            << this_thread::get_id()
            << endl;
    });

    worker_threads.post([] {
        cout << "Task B"
            << " runs in thread "
            << this_thread::get_id()
            << endl;
    });

    event_loop.post([] {
        cout << "Task C"
            << " runs in event thread "
            << this_thread::get_id()
            << endl;
    });

    worker_threads.post([] {
        cout << "Task D"
            << " runs in thread "
            << this_thread::get_id()
            << endl;
    });

    event_loop.post([] {
        cout << "task E"
            << " runs in event thread "
            << this_thread::get_id()
            << endl;
    });

    worker_threads.post([] {
        cout << "Task F"
            << " runs in thread "
            << this_thread::get_id()
            << endl;
    });

    worker_threads.post([] {
        cout << "Task G"
            << " runs in thread "
            << this_thread::get_id()
            << endl;
    });

    worker_threads.post([] {
        cout << "Task H"
            << " runs in thread "
            << this_thread::get_id()
            << endl;
    });

    worker_threads.join();
    event_loop.join();

    return 0;
}
