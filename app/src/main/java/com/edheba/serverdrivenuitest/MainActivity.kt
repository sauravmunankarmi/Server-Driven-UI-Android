package com.edheba.serverdrivenuitest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.flipkart.android.proteus.*
import com.flipkart.android.proteus.exceptions.ProteusInflateException
import com.flipkart.android.proteus.gson.ProteusTypeAdapterFactory
import com.flipkart.android.proteus.support.design.DesignModule
import com.flipkart.android.proteus.support.v4.SupportV4Module
import com.flipkart.android.proteus.support.v7.CardViewModule
import com.flipkart.android.proteus.support.v7.RecyclerViewModule
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Value
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.StringReader
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    val apiInterface = RetrofitClient.getUIRetrofit()!!.create(ApiInterface::class.java)

    //proteus
    var data: ObjectValue? = null
    var layout: Layout? = null
    var styles: Styles? = null
    var layouts: Map<String, Layout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiCall()
    }

    private fun apiCall(){
        val call: Call<UIResponse> = apiInterface.getUI()

        call.enqueue(object : Callback<UIResponse>{
            override fun onResponse(call: Call<UIResponse>, response: Response<UIResponse>) {
                try{
                    println("reposne => ${response.body().toString()}")
                    response.body()?.let { proteusJob(it) }
                }catch (e: Exception){
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UIResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "conn failure", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun proteusJob(response: UIResponse){

        proteus_layout.removeAllViews()
        val proteus = ProteusBuilder().register(SupportV4Module.create())
            .register(RecyclerViewModule.create())
            .register(CardViewModule.create())
            .register(DesignModule.create())
            .build()

        val adapter = ProteusTypeAdapterFactory(this)
        ProteusTypeAdapterFactory.PROTEUS_INSTANCE_HOLDER.proteus = proteus

        val layoutJsonString:String = Gson().toJson(response.layout)
        val dataJsonString:String = Gson().toJson(response.data)

        try {
            layout = adapter.LAYOUT_TYPE_ADAPTER.read(JsonReader(StringReader(layoutJsonString)))
            data = adapter.OBJECT_TYPE_ADAPTER.read(JsonReader(StringReader(dataJsonString)))
        } catch (e: Exception) {
            println("oi oi oi=== ${e.message} === mathi && ${layoutJsonString}\n\n === tala $dataJsonString")
        }

        val context: ProteusContext = proteus.createContextBuilder(this)
            .setLayoutManager(layoutManager)
            .setCallback(callback)
//            .setImageLoader(loader)
            .setStyleManager(styleManager)
            .build()

        val proteusLayoutInflater = context.inflater

        try {
            val view = proteusLayoutInflater.inflate(layout!!, data!!, proteus_layout, 0)
            proteus_layout.addView(view.asView)
        } catch (e: Exception) {
            println("hyaa ===> ${e.cause?.message}  $layoutJsonString \n\n${response.layout}\n\n\n\n ${response.data}")
        }

    }

    //tha navako fun
    private val styleManager: StyleManager = object : StyleManager() {
        override fun getStyles(): Styles? {
            return styles
        }
    }

    private val layoutManager: LayoutManager = object : LayoutManager() {
        override fun getLayouts(): Map<String, Layout>? {
            return layouts
        }
    }

    private val callback: ProteusLayoutInflater.Callback = object : ProteusLayoutInflater.Callback {
        override fun onUnknownViewType(
            context: ProteusContext,
            type: String,
            layout: Layout,
            data: ObjectValue,
            index: Int
        ): ProteusView {
            // TODO: instead return some implementation of an unknown view
            throw ProteusInflateException("Unknown view type '$type' cannot be inflated")
        }

        override fun onEvent(event: String, value: Value, view: ProteusView) {
            Log.i("ProteusEvent", value.toString())
        }
    }
}