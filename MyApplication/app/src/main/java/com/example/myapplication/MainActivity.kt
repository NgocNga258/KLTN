package com.example.myapplication


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock.sleep
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
//    @SuppressLint("MissingInflatedId")
    private lateinit var binding: ActivityMainBinding
    private var isled1 = false
    private var isled2 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // create a thread to get data from Azure REST API every
        val thread = Thread(Runnable {
            while (true) {

                val data = AzureRequest.instance.get_data(
                    "https://khoa-luan-iot-fpga-soc.azureiotcentral.com/api/query?api-version=2022-10-31-preview",
                    "{\n  \"query\": \"SELECT TOP 1 temperature, humidity, led1, led2 FROM dtmi:jevliijyr:f8dlpydboc ORDER BY \$ts DESC\"\n}",
                    "SharedAccessSignature sr=4e10195c-5017-4fb8-a687-d08f0b6b58ec&sig=cxj8ZIxwRZXRMrv5iYVuTRNon1xPo4xMczWd8JZaMuM%3D&skn=getdata&se=1720063074199",
                    "application/json"
                )
                runOnUiThread {
                    println(data.toString())
                    // Update your UI views here
                    binding.txtTemperature.text = data.temperature.toString()
                    binding.txtHumidity.text = data.humidity.toString()
                    if(data.led1 == 1){
                        isled1 = true
                        binding.imgled1.setImageResource(R.drawable.ic_light_on);
                    }
                    else{
                        isled1 = false
                        binding.imgled1.setImageResource(R.drawable.ic_light_off);
                    }
                    if(data.led2 == 1){
                        isled2 = true
                        binding.imgled2.setImageResource(R.drawable.ic_light_on);
                    }
                    else{
                        isled2 = false
                        binding.imgled2.setImageResource(R.drawable.ic_light_off);
                    }
                    binding.txtTime.text = data.time
                    binding.txtDay.text = data.date
                }
                Thread.sleep(500)
            }
        })
        thread.start()

        binding.imgled1.setOnClickListener {

            val deviceId = "esp8266"
            val authorization =
                "SharedAccessSignature sr=4e10195c-5017-4fb8-a687-d08f0b6b58ec&sig=7pkYf9gPYS%2BG0iQoLn6EfN13EOnV9tHCtSys%2FIdbEkM%3D&skn=postcommand&se=1720109621936" // Replace with your authorization toke
            if (isled1) {
                val commandName = "TurnOffLed1"
                val methodName = "TurnOffLed1"
                val data = AzureRequest.instance.sendCommand(
                    deviceId,
                    commandName,
                    methodName,
                    authorization
                )
                binding.imgled1.setImageResource(R.drawable.ic_light_off)
            }
            else {

                val commandName = "TurnOnLed1"
                val methodName = "TurnOnLed1"
                val data = AzureRequest.instance.sendCommand(
                    deviceId,
                    commandName,
                    methodName,
                    authorization
                )
                binding.imgled1.setImageResource(R.drawable.ic_light_on)
            }
            isled1 = !isled1
        }
        binding.imgled2.setOnClickListener {

            val deviceId = "esp8266"
            val authorization = "SharedAccessSignature sr=4e10195c-5017-4fb8-a687-d08f0b6b58ec&sig=7pkYf9gPYS%2BG0iQoLn6EfN13EOnV9tHCtSys%2FIdbEkM%3D&skn=postcommand&se=1720109621936" // Replace with your authorization toke
            if (isled2) {
                val commandName = "TurnOffLed2"
                val methodName = "TurnOffLed2"
                val data = AzureRequest.instance.sendCommand(
                    deviceId,
                    commandName,
                    methodName,
                    authorization
                )
                binding.imgled2.setImageResource(R.drawable.ic_light_off)
            }
            else {
                val commandName = "TurnOnLed2"
                val methodName = "TurnOnLed2"
                val data = AzureRequest.instance.sendCommand(
                    deviceId,
                    commandName,
                    methodName,
                    authorization
                )
                binding.imgled2.setImageResource(R.drawable.ic_light_on)
            }
            isled2 = !isled2
        }
    }
}
