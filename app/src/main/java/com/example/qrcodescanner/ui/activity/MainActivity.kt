package com.example.qrcodescanner.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.qrcodescanner.R
import com.example.qrcodescanner.databinding.ActivityMainBinding
import com.example.qrcodescanner.database.model.QrResults
import com.example.qrcodescanner.database.LocaleDataBase
import com.example.qrcodescanner.ui.adapter.MainViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewPager()
        setBottomNavigationListener()
        setViewPagerListener()

        val qrResult = QrResults(
            result = "Text",
            resultType = "TEXT",
            favourite = false,
            calendar = Calendar.getInstance()
        )
        LocaleDataBase.getAppDatabase(this)?.getQrDao()?.insertQrResults(qrResult)


    }

    private fun setViewPagerListener() {
        binding.viewPager.setOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.bottomNavigationView.selectedItemId = R.id.qrScanMenuId
                    }

                    1 -> {
                        binding.bottomNavigationView.selectedItemId = R.id.scannedResultMenuId
                    }

                    2 -> {
                        binding.bottomNavigationView.selectedItemId = R.id.favouriteScannedMenuId
                    }
                }
            }
        })
    }

    private fun setBottomNavigationListener() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.qrScanMenuId -> binding.viewPager.currentItem = 0
                R.id.scannedResultMenuId -> binding.viewPager.currentItem = 1
                R.id.favouriteScannedMenuId -> binding.viewPager.currentItem = 2
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun setViewPager() {
        binding.viewPager.adapter = MainViewPagerAdapter(supportFragmentManager)
        binding.viewPager.offscreenPageLimit = 2
    }


}
