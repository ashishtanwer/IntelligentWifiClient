Intelligent Wifi Client

Android based service to Intelligently switch between the different Wifi and Mobile Networks

STEPS DONE
1 Interfacing with Accelerometer sensor to get the raw readings.

2 Applying the high pass and low pass filter to the RAW readings. High pass will acceleration due to gravity =9.8. Low pass will give the external acceleration. We need the external
acceleration to detect if we are travelling.

3 Interfacing with GPS to get the latitude and longitude.

4 Getting the WIfi Connection available from Android Wifi Manager.

5 Showing all the above information as activity. The activity activity will start a Intelligent Monitoring Service.

My source code is here
https://github.com/ashishtanwer/IntelligentWifiClient
