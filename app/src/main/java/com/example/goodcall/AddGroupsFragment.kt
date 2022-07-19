package com.example.goodcall

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
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
 * Use the [AddGroupsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddGroupsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth



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
        return inflater.inflate(R.layout.fragment_add_groups, container, false)
    }

    override fun onStart() {
        super.onStart()
        db = Firebase.database
        auth = FirebaseAuth.getInstance()

        val createGroupButton: Button? = activity?.findViewById(R.id.createGroupButton)
        val joinGroupButton: Button? = activity?.findViewById(R.id.joinGroupButton) //TODO implement

        val cardView: CardView? = activity?.findViewById(R.id.cardView)
        val submitButton: Button? = activity?.findViewById(R.id.submitCodeButton)
        val codeEditText: EditText? = activity?.findViewById(R.id.editTextCode)
        
        //If they want to create a group:
        createGroupButton?.setOnClickListener {
            val ref = db.reference
            val groupKey = ref.push().key ?: "NONE"
            val uid = auth.currentUser!!.uid
            val username = auth.currentUser!!.displayName

            val groupName = "$username's group"
            val members: ArrayList<String> = ArrayList()
            members.add(auth.currentUser!!.displayName!!)

            //Add the group to the database with key group key and value group object
            ref.child(groupKey).setValue(Group(members, ArrayList(), groupName, groupKey))
            //Tell the user:
            Toast.makeText(requireContext(), "$username's group created!", Toast.LENGTH_SHORT).show()

            //Update user data
            ref.child(uid).get().addOnSuccessListener {
                //Is the value stored in it of type ArrayList<String>?
                if(it.value is ArrayList<*> && (it.value as ArrayList<*>).all {it is String}) {
                    //Update the value:
                    @Suppress("UNCHECKED_CAST")
                    val userGroups = it.value as ArrayList<String>
                    userGroups.add(groupKey)
                    ref.child(uid).setValue(userGroups)
                } else {
                    //Create a fresh arraylist and set the value to it:
                    val userGroups = ArrayList<String>()
                    userGroups.add(groupKey)
                    ref.child(uid).setValue(userGroups)
                }

            }
        }
        

        joinGroupButton?.setOnClickListener {
            cardView?.visibility = View.VISIBLE
            //Make button appear clickable when text is in the code edit text
            codeEditText?.doOnTextChanged { text, _, _, _ ->
                val length = text?.length ?: 0
                if (length > 0) {
                    submitButton?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
                else {
                    submitButton?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.vivid_tangerine))
                }
            }
            val uid = auth.currentUser!!.uid
            submitButton?.setOnClickListener {
                if(!codeEditText?.text.isNullOrBlank()) {
                    val ref = db.reference
                    val code = codeEditText?.text.toString().trim()
                    Log.d(TAG, "onStart: Code: $code")
                    ref.child(code).get().addOnSuccessListener {
                        if(it.value is HashMap<*, *>) {
                            Log.d(TAG, "onStart: Group found!")
                            @Suppress("UNCHECKED_CAST")
                            val groupMap = it.value as HashMap<String, Any?>
                            //Before trying to join the group check to see if the user is already in the group:
                            @Suppress("UNCHECKED_CAST") //I know what I'm doing I think
                            val members: ArrayList<String> = groupMap["members"] as ArrayList<String> // = java.util.ArrayList<kotlin.String>
                            if(members.contains(uid)) {
                                codeEditText?.error = "You cannot join a group you are already in."
                            } else {
                                //Update group data:
                                members.add(auth.currentUser!!.displayName!!)
                                groupMap["members"] = members
                                ref.child(code).setValue(groupMap)

                                //Update user data:
                                ref.child(uid).get().addOnSuccessListener {
                                    var userGroups = it.getValue<ArrayList<String>>()
                                    if(userGroups != null) {
                                        userGroups.add(code)
                                        ref.child(uid).setValue(userGroups)
                                    } else {
                                        userGroups = ArrayList<String>()
                                        userGroups.add(code)
                                        ref.child(uid).setValue(userGroups)
                                    }


                                }
                                Toast.makeText(requireContext(), "You joined ${groupMap["name"]}", Toast.LENGTH_SHORT).show()
                                cardView?.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to find group", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "onStart: " + it.value?.javaClass?.name)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Please try again.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    codeEditText?.error = "Please submit the group code here."
                }
            }
        }
        
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddGroupsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddGroupsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}