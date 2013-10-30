#include <stdio.h>
#include <opencv2/opencv.hpp>

int main(int argc, char** argv)
{
    cv::Mat mat(100, 200, CV_8U);
    std::cout << "Created a cv matrix. Yay?" << std::endl;
}
