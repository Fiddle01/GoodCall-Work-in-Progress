package com.example.goodcall

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onStart() {
        super.onStart()
         recyclerView = requireActivity().findViewById(R.id.group_chats_recycler)

        //Use a linear layout manager
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        //Get the data from Firebase

        db = Firebase.database
        auth = FirebaseAuth.getInstance()

        val ref = db.reference
        val groups = mutableListOf<Group>()
        Log.d(TAG, "onStart: ${auth.currentUser!!.uid}")
        ref.child(auth.currentUser!!.uid).get().addOnSuccessListener {
            Log.d(TAG, "onStart: success")
            val groupIds = it.getValue<ArrayList<String>>()
            Log.d(TAG, "onStart: here-1")
            if (groupIds != null) {
                Log.d(TAG, "onStart: here0")
                for (id in groupIds) {
                    Log.d(TAG, "onStart: here1")
                    ref.child(id).get().addOnSuccessListener {
                        val group = it.getValue<Group>()
                        if(group != null) {
                            groups.add(group)
                        }
                        val adapter = GroupAdapter(groups)
                        recyclerView.adapter = adapter
                    }
                }
            }
        }
        val adapter = GroupAdapter(groups)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
