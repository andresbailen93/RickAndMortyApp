package com.mango.android.rickmortyapp.ui.activities.list

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mango.android.rickmortyapp.databinding.ActivityListBinding
import com.mango.android.rickmortyapp.ui.activities.detail.DetailActivity.Companion.start
import com.mango.android.rickmortyapp.ui.activities.list.adapter.CharacterAdapter
import com.mango.android.rickmortyapp.ui.dialogs.getServerErrorDialog
import com.mango.android.rickmortyapp.ui.viewmodel.list.ListViewModel
import es.andres.bailen.domain.models.CharacterModel
import es.andres.bailen.domain.models.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private var mCharacterAdapter: CharacterAdapter =
        CharacterAdapter(object : CharacterAdapter.OnCharacterClickListener {
            override fun onCharacterClicked(character: CharacterModel?, imageView: ImageView?) {
                start(this@ListActivity, character!!.id, imageView)
            }
        })

    private val viewModel: ListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // init recycler view
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = mCharacterAdapter
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = viewModel.getCharacterList()
                binding.root.post {
                    when (response.status) {
                        DataResult.Status.SUCCESS -> {
                            mCharacterAdapter.bindData(response.data)
                        }
                        DataResult.Status.ERROR -> {
                            getServerErrorDialog().show()
                        }
                    }
                }
            } catch (e: ExecutionException) {
                e.printStackTrace()
                binding.root.post { getServerErrorDialog().show() }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                binding.root.post { getServerErrorDialog().show() }
            }
        }
    }

}