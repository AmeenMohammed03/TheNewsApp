package com.example.thenewsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.FragmentArticleBinding
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.viewModel.NewsViewModel


class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var newsViewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        newsViewModel = (activity as? NewsActivity)?.newsViewModel ?: return
        val article = args.article

        if (!article.url.isNullOrEmpty()) {
            binding.webView.apply {
                webViewClient = WebViewClient()
                loadUrl(article.url)
            }
        } else {
            // Handle the case where the article URL is null or empty
            Toast.makeText(requireContext(), "Article URL is null or empty", Toast.LENGTH_SHORT).show()
        }
    }
}