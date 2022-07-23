package com.alexandruleonte.demo.gallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.alexandruleonte.demo.R
import com.alexandruleonte.demo.databinding.FragmentPhotoGalleryBinding
import com.alexandruleonte.demo.gallery.api.FlickrApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.create
import java.lang.Exception
import java.util.concurrent.TimeUnit

private const val TAG = "PhotoGalleryFragment"
private const val POLL_WORK = "POLL_WORK"

class PhotoGalleryFragment : Fragment() {
    private var _binding:FragmentPhotoGalleryBinding? = null
    private var searchView: SearchView? = null
    private var pollingMenuItem: MenuItem? =null

    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val photoGalleryViewModel: PhotoGalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        binding.photoGrid.layoutManager = GridLayoutManager(context, 3)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoGalleryViewModel.uiState.collect { state ->
                    binding.photoGrid.adapter = PhotoListAdapter(
                        state.images
                    ) {
                        photoPageUri ->
                        findNavController().navigate(
                            PhotoGalleryFragmentDirections.showPhoto(
                                photoPageUri
                            )
                        )
                    }
                    searchView?.setQuery("car", false)
                    updatePollingState(state.isPolling)
                }
            }
        }
    }

    private fun updatePollingState(isPolling: Boolean) {
        val toggleItemTitle = if (isPolling) {
            R.string.start_polling
        } else {
            R.string.start_polling
        }
        pollingMenuItem?.setTitle(toggleItemTitle)

        if (isPolling) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
            val periodicRequest = PeriodicWorkRequestBuilder<PollWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                POLL_WORK, ExistingPeriodicWorkPolicy.KEEP, periodicRequest
            )
        } else {
            WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as? SearchView
        pollingMenuItem = menu.findItem(R.id.menu_item_toggle_polling)

        searchView?.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $queryText")
                    photoGalleryViewModel.setQuery(queryText)
                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextChange: $queryText")
                    return false
                }
            })
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                photoGalleryViewModel.setQuery("")
                true
            }
            R.id.menu_item_toggle_polling -> {
                photoGalleryViewModel.toggleIsPolling()
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        searchView = null
        pollingMenuItem = null
    }
//    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
//    private lateinit var photoRecyclerView: RecyclerView
//    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        retainInstance = true
//        setHasOptionsMenu(true)
//
//        photoGalleryViewModel =
//            ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
//
//        val responseHandler = Handler()
//        thumbnailDownloader =
//            ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
//                val drawable = BitmapDrawable(resources, bitmap)
//                photoHolder.bindDrawable(drawable)
//            }
//        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        viewLifecycleOwner.lifecycle.addObserver(
//            thumbnailDownloader.viewLifecycleObserver
//        )
//        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
//        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
//        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)
//        return view
//    }
//    companion object {
//        fun newInstance() = PhotoGalleryFragment()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        photoGalleryViewModel.galleryItemLiveData.observe(
//            viewLifecycleOwner,
//            Observer { galleryItems ->
//                photoRecyclerView.adapter = PhotoAdapter(galleryItems)
//            })
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        viewLifecycleOwner.lifecycle.removeObserver(
//            thumbnailDownloader.viewLifecycleObserver
//        )
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        lifecycle.removeObserver(
//            thumbnailDownloader.fragmentLifecycleObserver
//        )
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.fragment_photo_gallery, menu)
//
//        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
//        val searchView = searchItem.actionView as SearchView
//        searchView.apply {
//            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(queryText: String): Boolean {
//                    Log.d(TAG, "QueryTextSubmit: $queryText")
//                    photoGalleryViewModel.fetchPhotos(queryText)
//                    return true
//                }
//                override fun onQueryTextChange(queryText: String): Boolean {
//                    Log.d(TAG, "QueryTextChange: $queryText")
//                    return false
//                }
//            })
//            setOnSearchClickListener {
//                searchView.setQuery(photoGalleryViewModel.searchTerm, false)
//            }
//        }
//
//        val toggleItem = menu.findItem(R.id.menu_item_toggle_polling)
//        val isPolling = QueryPreferences.isPolling(requireContext())
//        val toggleItemTitle = if (isPolling) {
//            R.string.stop_polling
//        } else {
//            R.string.start_polling
//        }
//        toggleItem.setTitle(toggleItemTitle)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.menu_item_clear -> {
//                photoGalleryViewModel.fetchPhotos("")
//                true
//            }
//            R.id.menu_item_toggle_polling -> {
//                val isPolling = QueryPreferences.isPolling(requireContext())
//                if (isPolling) {
//                    WorkManager.getInstance().cancelUniqueWork(POLL_WORK)
//                    QueryPreferences.setPolling(requireContext(), false)
//                } else {
//                    val constraints = Constraints.Builder()
//                        .setRequiredNetworkType(NetworkType.UNMETERED)
//                        .build()
//                    val periodicRequest = PeriodicWorkRequest
//                        .Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
//                        .setConstraints(constraints)
//                        .build()
//                    WorkManager.getInstance().enqueueUniquePeriodicWork(POLL_WORK,
//                        ExistingPeriodicWorkPolicy.KEEP,
//                        periodicRequest)
//                    QueryPreferences.setPolling(requireContext(), true)
//                }
//                activity?.invalidateOptionsMenu()
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    private inner class PhotoHolder(private val itemImageView: ImageView)
//        : RecyclerView.ViewHolder(itemImageView) ,View.OnClickListener {
//        private lateinit var galleryItem: GalleryItem
//        init {
//            itemView.setOnClickListener(this)
//        }
//        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
//
//        fun bindGalleryItem(item: GalleryItem) {
//            galleryItem = item
//        }
//        override fun onClick(view: View) {
//            val intent = PhotoPageActivity
//                .newIntent(requireContext(), galleryItem.photoPageUri)
//            startActivity(intent)
//        }
//    }
//
//    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>)
//        : RecyclerView.Adapter<PhotoHolder>() {
//        override fun onCreateViewHolder(
//            parent: ViewGroup,
//            viewType: Int
//        ): PhotoHolder {
//            val view = layoutInflater.inflate(
//                R.layout.list_item_gallery,
//                parent,
//                false
//            ) as ImageView
//            return PhotoHolder(view)
//        }
//        override fun getItemCount(): Int = galleryItems.size
//        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
//            val galleryItem = galleryItems[position]
//            holder.bindGalleryItem(galleryItem)
//            val placeholder: Drawable = ContextCompat.getDrawable(
//                requireContext(),
//                R.drawable.bill_up_close
//            ) ?: ColorDrawable()
//            holder.bindDrawable(placeholder)
//            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
//        }
//    }
}