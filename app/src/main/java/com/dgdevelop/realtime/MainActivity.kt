package com.dgdevelop.realtime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import com.dgdevelop.realtime.model.Artist
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var artistNames: ArrayList<String>
    private lateinit var artists: ArrayList<Artist>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Traera los datos de manera persistente aunque no tenga conexion a internet
        // Lo guardara como cache
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        databaseReference = FirebaseDatabase.getInstance().reference // 4hcXkGJs87Sgd4SbBQTP

        artistNames = ArrayList()
        artists = ArrayList()

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, artistNames)

        lvArtist.adapter = arrayAdapter

        databaseReference.child(ARTISTS_NODE).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
               artistNames.clear()
                artists.clear()
                if(snapshot.exists()){
                    for (data in snapshot.children){
                        val artist = data.getValue(Artist::class.java)
                        Log.w(TAG, "Artist Name: ${artist?.name}")
                        artistNames.add(artist?.name!!)
                        artists.add(artist)
                    }
                }
                arrayAdapter.notifyDataSetChanged()
            }
        })

        lvArtist.setOnItemLongClickListener { adapterView, view, position, l ->
            val idArtist = artists[position].id
            artists.removeAt(position)
            artistNames.removeAt(position)
            databaseReference.child(ARTISTS_NODE).child(idArtist!!).removeValue()
            true
        }
    }

    fun createArtist(view: View){
        val artist = Artist(databaseReference.push().key!!, "Garbage", "Rock")
        databaseReference.child(ARTISTS_NODE).child(artist.id!!).setValue(artist)
    }

    companion object{
        private const val  ARTISTS_NODE = "Artists"
        private const val TAG = "Main"
    }
}