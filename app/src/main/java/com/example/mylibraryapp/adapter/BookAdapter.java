package com.example.mylibraryapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mylibraryapp.R;
import com.example.mylibraryapp.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final Context context;
    private List<Book> bookList;

    public BookAdapter(Context context, List<Book> bookList){
        this.context = context;
        this.bookList = bookList;
    }

    public void updateList(List<Book> newBooks){
        bookList = newBooks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position){
        Book book = bookList.get(position);

        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthors());

        // Glide ile kitap kapağını yükle
        Glide.with(context)
                .load(book.getThumbnail()) // Kitap kapağını yükler
                .placeholder(R.drawable.ic_book_placeholder) // Placeholder ekler
                .into(holder.imageViewCover);

        // Favori butonuna tıklanması durumunda işlem yapılır
        holder.btnFavorite.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                // Kullanıcı giriş yapmamışsa uyarı göster
                Toast.makeText(context, "Lütfen giriş yapın", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase'e favori olarak kaydet
            DatabaseReference favRef = FirebaseDatabase.getInstance()
                    .getReference("Favorites")
                    .child(user.getUid())
                    .child(book.getId() != null ? book.getId() : book.getTitle() + "_" + book.getAuthors());  // Eğer book.getId() null ise, title + authors kombinasyonu kullanılabilir

            favRef.setValue(book)
                    .addOnSuccessListener(unused -> Toast.makeText(context, "Favorilere eklendi", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Favori kaydı başarısız!", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount(){
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewCover;
        TextView textViewTitle, textViewAuthor;
        ImageButton btnFavorite;

        public BookViewHolder(@NonNull View itemView){
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.imageViewCover);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
