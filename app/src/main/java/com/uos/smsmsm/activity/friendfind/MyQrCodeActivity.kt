package com.uos.smsmsm.activity.friendfind

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityMyQrCodeBinding


class MyQrCodeActivity : BaseActivity<ActivityMyQrCodeBinding>(R.layout.activity_my_qr_code){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.slide_up, R.anim.slide_up_out)
        binding.activity = this
        val uid = intent.getStringExtra("uid")
        uid?.let {

            generateRQCode(it)
        }?:{
            Toast.makeText(rootContext,"Qr 코드를 만들 수 없습니다.", Toast.LENGTH_LONG).show()
            finish()
        }()
    }
    fun generateRQCode(contents: String?) {
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitmap: Bitmap = toBitmap(qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, 100, 100))
            binding.imgMyQrCodeQrImg.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
    private fun toBitmap(matrix: BitMatrix): Bitmap {
        val height = matrix.height
        val width = matrix.width
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }
    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.slide_down, R.anim.slide_down_out)
    }
    fun close(v: View){
        finish()
    }

}