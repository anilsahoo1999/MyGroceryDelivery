package com.aks.mygrocery.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.aks.mygrocery.R
import com.aks.mygrocery.adapter.CategoryAdapter
import com.aks.mygrocery.base.BaseActivity
import com.aks.mygrocery.base.BaseFragment
import com.aks.mygrocery.databinding.FragmentSeeAllCategoryBinding
import com.aks.mygrocery.fragment.HomeFragment.Companion.categoryList

class SeeAllCategoryFragment : BaseFragment<FragmentSeeAllCategoryBinding>() {

    override fun getFragmentId(): Int {
        return R.layout.fragment_see_all_category
    }

    private var categoryAdapter: CategoryAdapter?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).binding.bottomNavView.visibility = View.GONE

        binding.categoryShimmer.startShimmer()
        val colorList = arrayListOf<Int>()
        colorList.add(R.color.md_amber_100)
        colorList.add(R.color.md_cyan_100)
        colorList.add(R.color.md_lime_100)
        colorList.add(R.color.md_red_100)
        colorList.add(R.color.md_teal_200)
        colorList.add(R.color.md_amber_100)
        colorList.add(R.color.md_cyan_100)
        colorList.add(R.color.md_lime_100)
        colorList.add(R.color.md_red_100)
        colorList.add(R.color.md_teal_200)

        val colorListModified = arrayListOf<Int>()
        if (categoryList.size > 0) {
            for (i in 0..(categoryList.size/10)+1) {
                for (j in 0 until colorList.size-1) {
                    colorListModified.add(colorList[j])
                }
            }
            categoryAdapter = CategoryAdapter(colorListModified)
            Handler().postDelayed({
                binding.categoryShimmer.stopShimmer()
                binding.categoryShimmer.visibility = View.GONE
                binding.recyclerCategory.visibility = View.VISIBLE
                binding.recyclerCategory.adapter = categoryAdapter
                categoryAdapter!!.differ.submitList(categoryList)
            },1000)
        }else{
            binding.categoryShimmer.stopShimmer()
            binding.categoryShimmer.visibility = View.GONE
            binding.imageNotFound.visibility = View.VISIBLE
        }

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        categoryAdapter?.onItemClickCallBackListener { categoryModel, i ->
            val bundle = Bundle().apply {
                putString("CATEGORY_ID",categoryModel.categoryID)
                putString("CATEGORY_NAME", categoryModel.name)
            }
            findNavController().navigate(R.id.action_seeAllCategoryFragment_to_seeAllProductFragment,bundle)
        }




    }

}