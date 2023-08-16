- File code để đưa dữ liệu đọc được từ cảm biến và led lên Azure IoT Central sẽ nằm trong đường dẫn: azure-iot-sdk-c\samples\dockerbuilds\myapp\iothub_cross_compile_simple_sample.c
- Sử dụng phần mềm Ubuntu22-04.
- Đầu tiên, Set up a Linux development environment:
	# For Ubuntu, you can use apt-get to install the right packages: 
		sudo apt-get update
		sudo apt-get install -y git cmake build-essential curl libcurl4-openssl-dev libssl-dev uuid-dev ca-certificates
	#V erify that CMake is at least version 2.8.12:
		cmake --version
	# Verify that gcc is at least version 4.4.7:
		gcc --version
	# Clone the latest release of SDK to your local machine using the tag name you found:
		git clone -b lts_01_2023 https://github.com/Azure/azure-iot-sdk-c.git
		cd azure-iot-sdk-c
		git submodule update --init


	#Build the C SDK on Linux, To build the SDK:
			cd azure-iot-sdk-c
			mkdir cmake
			cd cmake
			cmake ..
			cmake --build .  # append '-- -j <n>' to run <n> jobs in parallel
			
- Sử dụng Cross Compiling the SDK in a Docker Container.txt
	# Change directory to your Azure IoT SDK cloned repository root
		cd azure-iot-sdk-c
	# Work in this directory or two copies of the myapp directory will be required
		cd samples/dockerbuilds
	# Cross compile the SDK (Lưu ý phải mở docket desktop)
		docker build -t armiotbuild:latest ./ARM --network=host
	# Build the application against the SDK
		docker build -t armiotapp:latest . --network=host --file ./ARM/Dockerfile_adjunct
		id=$(docker create armiotapp)
	# Copy application to home directory
		docker cp $id:/home/builder/myapp/cmake/myapp ~/myapp_arm
		docker rm -v $id
	
- Sau khi biên dịch chéo trên Ubuntu22-04 xong copy myapp_arm xuống Board De10 để thực thi file.
