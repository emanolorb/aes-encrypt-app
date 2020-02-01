package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import javax.crypto.Cipher
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        btEncript.setOnClickListener(View.OnClickListener {

            val noncebyte = getNonce(12)
            val testVal = etString.text.toString()
            val testBA = testVal.toByteArray(Charsets.UTF_8)
            val keybyte = hexStringToByteArray("160ee4da151a47b33347384eb4ee541a")
            // val noncebyte = hexStringToByteArray("8aacf99a3b6898cfed10518b")

            var nonceStrHex = noncebyte.toHexString()
            println("----------noncestr-----------")
            println(nonceStrHex)
            println("------------------------------")


            var resultado = encryptGcm(testBA, keybyte, noncebyte)
            println("resultado encryptGcm---------------")
            println(resultado.ciphertext.contentToString())
            println(resultado.ciphertext.toString())
            var strhex = resultado.ciphertext.toHexString()
            println(strhex)

            val output1 = String(resultado.ciphertext, Charsets.UTF_8)
            println(output1)
            println("-------------------------------")
            var resultado2 = decryptGcm(resultado,keybyte)
            println("resultado2 ----------------")
            println(resultado2.contentToString())

            val output2 = String(resultado2, Charsets.UTF_8)
            println(output2)
            tvRestultado.text = strhex
            println("-------------------------------")
            Toast.makeText(this, "hola mundo", Toast.LENGTH_LONG).show()
        })
    }

    fun encryptGcm(plaintext: ByteArray, key: ByteArray, nonce: ByteArray): Ciphertext {
        println("entrando en encript ----------------")

        val cipher = Cipher.getInstance("AES_128/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES_128")

        val gcmSpec = GCMParameterSpec(128, nonce) // 128 bit authentication tag

        println("despues del auth tag ----------------")
        println("keyspect ----------------")
        println(keySpec)
        println("gcmSpec ----------------")
        println(gcmSpec)


        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

        val ciphertext = cipher.doFinal(plaintext)
        return Ciphertext(ciphertext, nonce)
    }

    class Ciphertext(val ciphertext: ByteArray, val iv: ByteArray)

    fun decryptGcm(ciphertext: Ciphertext, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES_128/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES_128")

        val gcmSpec = GCMParameterSpec(128, ciphertext.iv) // 128 bit authentication tag

        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)

        val plaintext = cipher.doFinal(ciphertext.ciphertext)
        return plaintext
    }

    fun hexStringToByteArray(string: String) : ByteArray {
        val hexChunked = ArrayList<Int>()
        var bytesArray = ByteArray(string.count()/2)

        for (i in 0 until string.count() step 2) {
            hexChunked.add(Integer.parseInt(string.substring(i, i+2), 16))
        }
        println(hexChunked)
        for (i in 0 until hexChunked.count()) {
            bytesArray[i] = hexChunked[i].toByte()
        }
        println("bytearray paricin----------------------------")
        println(bytesArray)

        return bytesArray
    }
    fun ByteArray.toHexString() : String {
        return this.joinToString("") {
            java.lang.String.format("%02x", it)
        }
    }
    fun getNonce(sizeVar:Int):ByteArray
    {
        val b = ByteArray(sizeVar)
        Random().nextBytes(b)
        return b
    }
}
