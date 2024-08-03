package com.bics.expense.doctormodule.fragment.doctorAppoinment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DocumentAdapter(
    private val context: Context,
    private var documents: List<AppointmentNote>,
    private val imageClickListener: ImageClickListener,
    private val onDeleteDocumentListener: DoctorAppointmentFragment // Add this line
) : RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {

    interface ImageClickListener {
        fun onImageClicked(imageUrl: String)
    }
    interface OnDeleteDocumentListener {
        fun onDocumentDeleted()
    }

    fun updateDocuments(newDocuments: List<AppointmentNote>) {
        documents = newDocuments
        notifyDataSetChanged()
    }
    fun createDeleteDocumentRequestBody(appointmentNotesID: String, fileAttachment: String): RequestBody {
        val json = JSONObject()
        json.put("appointmentNotesID", appointmentNotesID)
        json.put("fileAttachment", fileAttachment)

        val mediaType = "application/json".toMediaTypeOrNull()
        return RequestBody.create(mediaType, json.toString())
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val documentType: TextView = itemView.findViewById(R.id.typeName)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val imageName: TextView = itemView.findViewById(R.id.imageNameDocument)
        private val deleteBtn: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(note: AppointmentNote) {
            documentType.text = note.documentType
            imageName.text = note.fileName

            // Set click listener for delete button


            val baseUrl = "https://myclinicsapi.bicsglobal.com"
            val fullImageUrl = "$baseUrl${note.fullDocumentpath}"

            // Load image using Glide
            Glide.with(itemView)
                .load(fullImageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        e?.printStackTrace() // Log the exception
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(imageView)

            imageView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullImageUrl))
                context.startActivity(browserIntent)
                imageClickListener.onImageClicked(fullImageUrl)
            }


            deleteBtn.setOnClickListener {
                // Call API to delete document
                deleteDocument(note.appointmentNotesID,note.fileAttachments)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(documents[position])
    }

    override fun getItemCount(): Int {
        return documents.size
    }

    private fun deleteDocument(appointmentNotesID: String, fileAttachment: String) {


        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Delete Document")
        alertDialogBuilder.setMessage("Are you sure you want to delete this document?")

        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            // User clicked Yes button
            val requestBody = createDeleteDocumentRequestBody(appointmentNotesID, fileAttachment)


            val sharedPreferences = context.getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", "")

            RetrofitClient.setAuthToken(token)

            RetrofitClient.apiService.deleteDocumentBasedOnAppointmentNotesID(requestBody)

                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Document deleted successfully", Toast.LENGTH_SHORT).show()
                            onDeleteDocumentListener.onDocumentDeleted() // Notify document deleted

                            // Optionally, update your local document list if needed
                        } else {
                            Toast.makeText(context, "Failed to delete document", Toast.LENGTH_SHORT).show()
                            // Handle specific errors based on response codes or error messages
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Error deleting document: ${t.message}", Toast.LENGTH_SHORT).show()
                        // Handle failure scenario, e.g., logging or retry mechanism
                    }
                })
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
            // User clicked No button or cancelled dialog
            // Optionally, handle cancellation or do nothing
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}