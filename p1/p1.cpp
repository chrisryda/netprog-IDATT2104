#include <iostream>
#include <thread>
#include <vector>
#include <math.h>

using namespace std;

bool isPrime(int n) {
    for (int i = 2; i < n; ++i) {
        if (n % i == 0) {
            return false;
        }
    }
    return true;
}

vector<int> getPrimesInRange(int lower, int upper) {
    vector<int> primes_in_range = {}; 
    for (int i = lower; i <= upper; ++i) {
        if (i == 1 || i == 0) {
            continue;
        }
        if (isPrime(i)) {
            primes_in_range.emplace_back(i);
        }
    }
    return primes_in_range;
}

vector<vector<int>> getBatches(int lower, int upper, int n_threads) {
    if (upper <= lower) {
        throw invalid_argument("Lower and upper limit does not define an interval.");
    }

    vector<vector<int>> batches;
    int interval = upper - lower;
    int primes_per_thread = floor(interval/n_threads);

    vector<int> batch = {};
    for (int i = 0; i < n_threads; ++i) {
        batch.emplace_back(lower + primes_per_thread * i);
        batch.emplace_back(lower + primes_per_thread * (i+1));

        if (i == (n_threads-1) && interval%n_threads != 0) {
            batch[1] += interval%n_threads;
        }
        batches.emplace_back(batch);
        batch.clear();
    }

    cout << "Number of intervals: " << batches.size() << endl;
    for (size_t i = 0; i < batches.size(); ++i) {
        cout << "Interval " << i+1 << ": [ " << batches[i][0] << ", " << batches[i][1] << " ]" << endl;
    }
    cout << "\n\n";

    return batches;
}

int main() {
    int lower = 300;
    int upper = 600;
    int n_threads = 8;
    if (n_threads > upper - lower) {
        n_threads = upper - lower;        
    } 
    vector<vector<int>> primes(n_threads);

    vector<int> batch;
    vector<vector<int>> batches = getBatches(lower, upper, n_threads);
    
    vector<thread> threads;
    threads.reserve(n_threads);
    for (int i = 0; i < n_threads; ++i ) {
        threads.emplace_back([i, &batch, &batches, &primes] {
            batch = batches[i];
            primes[i] = getPrimesInRange(batch[0], batch[1]);
        });
    }

    for (auto &thread : threads) {
        thread.join();
    }

    cout << "Primes:" << endl;
    for (size_t i = 0; i < primes.size(); ++i) {
        for (size_t j = 0; j < primes[i].size(); ++j) {
            cout << primes[i][j] << endl;
        }
    }
}
