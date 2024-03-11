#include <thread>
#include <iostream>
#include <omp.h>
#include <vector>
#include <cmath>
#include <fstream>
#include <cstdlib>
#include <string>
#include <sstream>
#include <Windows.h>
#include <stdio.h>
#include <intrin.h>

int resInt[3];
float resFloat[3];
long int testDataInt[131072];
long float testDataFloat[131072];



void test1(int n,int index) {
	if (index % 2)
		resInt[0] += n;
	else
		resInt[0] -= n;
	return;

}
void test2(int n,int index) {

	switch (index) {
	case 1:
		resInt[1] += n;
		break;
	case 2:
		resInt[1] -= n;
		break;
	case 3:
		resInt[1] *= n;
		break;
	case 4:
		resInt[1] /= n;
		break;
	default:
		break;

	}

	return;

}
void test3(int n) {
	int index = rand() % 4;
	index++;
	switch (index) {
	case 1:
		resInt[2] += n;
		break;
	case 2:
		resInt[2] -= n;
		break;
	case 3:
		resInt[2] *= n;
		break;
	case 4:
		resInt[2] /= n;
		break;
	default:
		break;

	}

	return;

}
void test4(float n,int index) {
	if (index % 2)
		resFloat[0] += n;
	else
		resFloat[0] -= n;
	return;
}
void test5(float n,int index) {
	switch (index) {
	case 1:
		resFloat[1] += n;
		break;
	case 2:
		resFloat[1] -= n;
		break;
	case 3:
		resFloat[1] *= n;
		break;
	case 4:
		resFloat[1] /= n;
		break;
	default:
		break;

	}

	return;

}
void test6(float n) {
	int index = rand() % 4;
	index++;
	switch (index) {
	case 1:
		resFloat[2] += n;
		break;
	case 2:
		resFloat[2] -= n;
		break;
	case 3:
		resFloat[2] *= n;
		break;
	case 4:
		resFloat[2] /= n;
		break;
	default:
		break;

	}

	return;

}

void threadFunction(int n1,float n2,int testNr,int index) {
	switch (testNr) {
	case 1:
		test1(n1,index);
		break;
	case 2:
		test2(n1,index);
		break;
	case 3:
		test3(n1);
		break;
	case 4:
		test4(n2,index);
		break;
	case 5:
		test5(n2,index);
		break;
	case 6:
		test6(n2);
		break;

	default:
		break;
	}
}



int main() {

	
	

	int testCase;
	std::cout << "Select a test from 1 to 6:";
	std::cout << std::endl;
	std::cin >> testCase;


	std::vector<double> executionTimes;  

	std::vector<std::pair<int, double>> data; 


	for (int i = 0;i < 131072;i++) {
		testDataFloat[i] = i + 1.0;
		testDataInt[i] = i + 1;
	}
	int nrThreads = 1;
	float minex = 9999999;
	float maxex = 0;
	int minthreads, maxthreads;
	float avg[11];
	int loopNr = 0;



	while (nrThreads < 1025) {
		
		int intDataIndex = 0;
		int floatDataIndex = 0;
		std::thread threads[1024];
		double start_time = omp_get_wtime();
		
		
		while (intDataIndex < 131072 && floatDataIndex < 131072) {

			for (int i = 0;i < nrThreads;i++) {
				std::thread t(threadFunction, testDataInt[intDataIndex], testDataFloat[floatDataIndex], testCase, i);
				intDataIndex++;
				floatDataIndex++;
				threads[i] = std::move(t);

			}
			for (int i = 0; i < nrThreads; i++) {
				threads[i].join();
			}
			
			
		}
		double end_time = omp_get_wtime();
		double time_taken = double(end_time - start_time);
		if (time_taken > maxex)
			{ 
			maxex = time_taken;
			maxthreads = nrThreads;
			}
		if (time_taken < minex)
		{
			minex = time_taken;
			minthreads = nrThreads;
		}
		avg[loopNr] = time_taken / nrThreads;
		executionTimes.push_back(time_taken);
	
		for (int i = 0;i < 3;i++) {
			resInt[i] = 0;
			resFloat[i] = 0.0;
		}
		
		nrThreads *= 2;
		loopNr++;
	}
	
	for (int i = 0; i < executionTimes.size(); ++i) {
		data.push_back(std::make_pair(pow(2,i), executionTimes[i]));
	}
	std::vector<int> x;
	std::vector<double> y;
	for (const auto& point : data) {
		x.push_back(point.first);
		y.push_back(point.second);
	}
	for (int i = 1;i <= 11;i++)
		std::cout << y.at(i - 1) << " seconds with " << x.at(i - 1)<<" threads; Average execution time/thread: "<<avg[i-1]<<" seconds." << std::endl;
	std::cout << "Minimum execution time: " << minex << " seconds achieved with " << minthreads << " threads\n";
	std::cout << "Maximum execution time: " << maxex << " seconds achieved with " << maxthreads << " threads\n";
	float score=100/minex;
	std::cout << "Final score: " << score  << "\n";
	std::ofstream outfile("data.csv");
	for (int i = 0; i < x.size(); ++i) {
		outfile << x[i] << "," << y[i] << "\n";
	}
	outfile.close();

	std::ostringstream commandStream;
	commandStream << "python plot.py data.csv "  << " " << score << " " << testCase;
	std::string command = commandStream.str();
	
	int result = std::system(command.c_str());
	
	

	return 0;
}
