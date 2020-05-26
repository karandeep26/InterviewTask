package com.interviewtask

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.interviewtask.databinding.ActivityMainBinding
import com.interviewtask.worker.ServiceWorker
import com.interviewtask.worker.Task
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InterruptedIOException


class MainActivity : AppCompatActivity() {

    companion object {
        const val IMAGE_1 = "https://homepages.cae.wisc.edu/~ece533/images/airplane.png"
        const val IMAGE_2 = "https://homepages.cae.wisc.edu/~ece533/images/arctichare.png"
        const val TAG = "MainActivity"
    }

    var serviceWorker1 = ServiceWorker("service_worker_1")
    var serviceWorker2 = ServiceWorker("service_worker_2")

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val okHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonOne.setOnClickListener {
            binding.textViewOne.text = getString(R.string.loading_text)
            fetchAndSetImage(IMAGE_1, binding.imageViewOne, serviceWorker1)
        }

        binding.buttonTwo.setOnClickListener {
            binding.textViewTwo.text = getString(R.string.loading_text)
            fetchAndSetImage(IMAGE_2, binding.imageViewTwo, serviceWorker2)
        }
    }

    private fun fetchAndSetImage(url: String, imageView: ImageView, serviceWorker: ServiceWorker) {
        serviceWorker.addTask(object : Task<Bitmap?> {
            override fun onExecuteTask(): Bitmap? {
                val request: Request = Request.Builder().url(url).build()
                return try {
                    val response: Response = okHttpClient.newCall(request).execute()
                    response.body?.let {
                        BitmapFactory.decodeStream(it.byteStream())
                    }
                } catch (e: InterruptedIOException) {
                    //Log exception
                    return null
                } catch (e: IOException) {
                    return null
                }
            }

            override fun onTaskComplete(result: Bitmap?) {
                if (result != null) {
                    imageView.setImageBitmap(result)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceWorker2.shutDownTasks()
        serviceWorker1.shutDownTasks()
    }

}
