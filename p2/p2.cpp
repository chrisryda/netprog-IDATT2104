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
    
    bool stop_threads = false;
    bool stop_threads_on_completion = false;
    
    list<function<void()>> tasks;
    condition_variable cv;
    mutex mtx;

    Workers () {}
    Workers (int n_threads_) : n_threads(n_threads_) {}

    void start() {
        for (int i = 0; i < n_threads; i++) {
            worker_threads.emplace_back([this] {
                while (true) {
                    function<void()> task;
                    {
                        unique_lock<mutex> lock(mtx);
                        while (tasks.empty() && !stop_threads) {
                            cv.wait(lock);
                        }
                        if (tasks.empty() && stop_threads) {
                            return;
                        }
                        task = *tasks.begin();
                        tasks.pop_front();
                    }
                    task();
                    
                    unique_lock<mutex> lock(mtx);
                    if (stop_threads_on_completion && tasks.empty()) {
                        stop_threads = true;
                        lock.unlock();
                        cv.notify_all(); // Notify all other worker threads to stop
                        return;
                    }
                }
            });
        }
    }   

    void post(const function<void()> &task) {
        {
			unique_lock<mutex> lock(mtx);
			tasks.emplace_back(task);
		}
		cv.notify_one();
    }

    void post_timeout(const function<void()> &task, int ms) {
		this_thread::sleep_for(chrono::milliseconds(ms));
		post(task);
	}

    void stop() {
		{
			unique_lock<mutex> lock(mtx);
			if (tasks.empty()) { 
				stop_threads = true;
				lock.unlock();
				cv.notify_all();
			} else {
				stop_threads_on_completion = true;
            }
		}
	}

    void join() {
        for (auto &t : worker_threads) {
            t.join();
        }
    }
};

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

    worker_threads.stop();
    event_loop.stop();

    worker_threads.join();
    event_loop.join();

    return 0;
}
