package com.code4rox.medialoaderx

import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.code4rox.medialoaderx.databinding.FragmentAllFilesBinding


class AllFilesFragment : Fragment() {


    private lateinit var binding: FragmentAllFilesBinding

    private val allFilesRecyclerAdapterX: AllFilesRecyclerAdapterX by lazy {
        AllFilesRecyclerAdapterX()
    }
    private val allFolderRecyclerAdapterX: AllFolderRecyclerAdapterX by lazy {
        AllFolderRecyclerAdapterX()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAllFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.adapter = allFilesRecyclerAdapterX
        binding.rvFolder.adapter = allFolderRecyclerAdapterX


        arguments?.getString(FRAG_TYPE)?.apply {

            when (this) {

                IMAGE_TYPE -> {
                    ImageLoaderX(requireContext()).apply {
                        mSortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
                    }.getAllImages(
                        onImageListSuccess = {
                            allFilesRecyclerAdapterX.setData(it)
                        }, onImageFolderListSuccess = {
                            allFolderRecyclerAdapterX.setData(it)
                        })
                }
                VIDEO_TYPE -> {
                    VideoLoaderX(requireContext()).getAllVideos(
                        onVideoListSuccess = {
                            allFilesRecyclerAdapterX.setData(it)
                        }, onVideoFolderListSuccess = {
                            allFolderRecyclerAdapterX.setData(it)
                        })
                }
                AUDIO_TYPE -> {
                    AudioLoaderX(requireContext()).getAllAudios({
                        allFilesRecyclerAdapterX.setData(it)
                    }, {
                        allFolderRecyclerAdapterX.setData(it)
                    })
                }
                FILE_TYPE -> {
                    FileLoaderX(requireContext()).apply {
                        mFilterFileExt = arrayOf("apk")
                    }.getAllFiles({
                        allFilesRecyclerAdapterX.setData(it)

                    }, {
                        allFolderRecyclerAdapterX.setData(it)

                    })
                }

            }
        }


    }

}