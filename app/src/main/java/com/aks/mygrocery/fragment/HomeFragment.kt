package com.aks.mygrocery.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.aks.mygrocery.R
import com.aks.mygrocery.adapter.BannerAdapter
import com.aks.mygrocery.adapter.CategoryAdapter
import com.aks.mygrocery.app.MyGroceryApp
import com.aks.mygrocery.base.BaseFragment
import com.aks.mygrocery.databinding.FragmentHomeBinding
import com.aks.mygrocery.models.BannerModel
import com.aks.mygrocery.models.CategoryModel
import com.aks.mygrocery.utils.HorizontalMarginItemDecoration
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun getFragmentId(): Int {
        return R.layout.fragment_home
    }

    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var categoryList: List<CategoryModel>? = null
    private var colorList: ArrayList<Int>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeView()



    }

    private fun initializeView() {

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        getCurrentLocation()

        val arrayList = ArrayList<BannerModel>()
        colorList = arrayListOf()
        colorList?.add(R.color.md_amber_100)
        colorList?.add(R.color.md_cyan_100)
        colorList?.add(R.color.md_lime_100)
        colorList?.add(R.color.md_red_100)
        colorList?.add(R.color.md_teal_200)


        repeat(2) {
            arrayList.add(
                BannerModel(
                    "https://www.thoughtco.com/thmb/gtl-IotmuSFI9Li_8OJtaPXGrms=/2592x2592/smart/filters:no_upscale()/89538987-56a1316f3df78cf772684961.jpg",
                    "rose3"
                )
            )
            arrayList.add(
                BannerModel(
                    "https://www.floraqueen.com/blog/wp-content/uploads/2020/02/shutterstock_552296878.jpg",
                    "rose2"
                )
            )

            arrayList.add(
                BannerModel(
                    "https://www.thoughtco.com/thmb/gtl-IotmuSFI9Li_8OJtaPXGrms=/2592x2592/smart/filters:no_upscale()/89538987-56a1316f3df78cf772684961.jpg",
                    "rose3"
                )
            )
        }
        bannerAdapter = BannerAdapter(bannerList = arrayList)
        binding.bannerViewPager.adapter = bannerAdapter
        binding.wormDotsIndicator.attachTo(binding.bannerViewPager)
        binding.bannerViewPager.offscreenPageLimit = 1

        binding.bannerViewPager.setCurrentItem(0, true)

        /*        val nextItemVisiblePx = 80
        val currentItemHorizontalMarginPx = 50
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        binding.bannerViewPager.setPageTransformer { page, position ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.25f * abs(position))
            // If you want a fading effect uncomment the next line:
            // page.alpha = 0.25f + (1 - abs(position))
        }*/
        val itemDecoration = HorizontalMarginItemDecoration()
//        binding.bannerViewPager.addItemDecoration(itemDecoration)
        autoScrollPosition()
        getAllCategory()
    }

    private val countDownTimer = object : CountDownTimer(2000, 1000) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            autoScrollPosition()
        }
    }

    private fun autoScrollPosition() {
        val currentPosition: Int = binding.bannerViewPager.currentItem
        if (currentPosition + 1 == bannerAdapter.itemCount) {
            binding.bannerViewPager.currentItem = 0
        } else {
            binding.bannerViewPager.currentItem = currentPosition + 1
        }
        countDownTimer.cancel()
        countDownTimer.start()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isLocationEnabled()) {
                //finally get latitude and longitude
                fetchLatitudeLongitude()

            } else {
                //open setting here
                Toast.makeText(requireActivity(), "Please turn on Internet", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun fetchLatitudeLongitude() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        fusedLocationProvider.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            val location: Location? = task.result
            if (location == null) {
                Toast.makeText(requireActivity(), "Unable to get location", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Log.e("Location", "fetchLatitudeLongitude: latitude = ${location.latitude}")
                Log.e("Location", "fetchLatitudeLongitude: longitude = ${location.longitude}")

                getAddressFromLocation(location)
            }
        }
    }

    private fun getAddressFromLocation(location: Location) {

        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if (address != null && address.isNotEmpty()) {
            val currentAddress = address[0].getAddressLine(0)
            val city = address[0].locality
            val state = address[0].adminArea
            val country = address[0].countryName
            val postalCode = address[0].postalCode
            val knownName = address[0].featureName

//            binding.txtLocation.text = currentAddress
        }

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestLocationPermission() {

        requestLaunchLocationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

    }

    private val requestLaunchLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.forEach { result ->
                if (!result.value) {
                    //permission denied
                    Toast.makeText(
                        requireContext(),
                        "Please allow all permission",
                        Toast.LENGTH_SHORT
                    ).show()
                    requestLocationPermission()
                } else {
                    //permission granted
                    Toast.makeText(requireActivity(), "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                    getCurrentLocation()
                }
            }
        }


    private fun getAllCategory() {
        binding.categoryShimmer.startShimmer()
        val firebaseDb = MyGroceryApp.instance.firebaseFirestore
        firebaseDb.collection("AdminMasterDetails").document("bcI5ARwAoHMLLQGdIXlHILEnlZ63")
            .collection("CategoryList").get().addOnSuccessListener { documentSnapshot ->
                categoryList = arrayListOf()
                categoryList = documentSnapshot.toObjects(CategoryModel::class.java)
                if (categoryList?.size!! > 0) {
                    binding.categoryShimmer.stopShimmer()
                    categoryAdapter = CategoryAdapter(
                        categoryList as List<CategoryModel>,
                        colorList!!
                    )
                    binding.categoryShimmer.visibility = View.INVISIBLE
                    binding.recyclerCategory.visibility = View.VISIBLE
                    binding.recyclerCategory.adapter = categoryAdapter
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

}