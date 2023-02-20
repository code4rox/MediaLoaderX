package com.code4rox.medialoaderx

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.code4rox.medialoaderx.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions.check


const val FRAG_TYPE = "FRAG_TYPE"
const val IMAGE_TYPE = "IMAGE_TYPE"
const val VIDEO_TYPE = "VIDEO_TYPE"
const val AUDIO_TYPE = "AUDIO_TYPE"
const val FILE_TYPE = "FILE_TYPE"

class MainActivity : AppCompatActivity() {

    private lateinit var adapterPager: MainPagerAdapter


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val permissions =
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        check(this, permissions, null, null, object : PermissionHandler() {
            override fun onGranted() {
                initViewPager()
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                super.onDenied(context, deniedPermissions)
                finish()
            }
        })
    }

    private fun initViewPager() {

        adapterPager = MainPagerAdapter(this).apply {
            addFragment(AllFilesFragment().apply { arguments = Bundle().apply { putString(FRAG_TYPE, IMAGE_TYPE) } })
            addFragment(AllFilesFragment().apply { arguments = Bundle().apply { putString(FRAG_TYPE, VIDEO_TYPE) } })
            addFragment(AllFilesFragment().apply { arguments = Bundle().apply { putString(FRAG_TYPE, AUDIO_TYPE) } })
            addFragment(AllFilesFragment().apply { arguments = Bundle().apply { putString(FRAG_TYPE, FILE_TYPE) } })
        }
        binding.vpMain.adapter = adapterPager

        initTabLayout()
    }

    private fun initTabLayout() {

        val tabTitles: Array<String> by lazy {
            arrayOf(
                getString(R.string.tab_images),
                getString(R.string.tab_video),
                getString(R.string.tab_audio),
                getString(R.string.tab_file),
            )
        }

        TabLayoutMediator(
            binding.tlHome, binding.vpMain
        ) { tab, position ->
            tab.text = tabTitles[position]

        }.attach()

    }
}