package com.example.mylibraryapp.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mylibraryapp.R;
import com.example.mylibraryapp.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final Context context;
    private List<Book> bookList;
    private final OnBookClickListener listener;
    private final boolean fromFavoritesPage; // Favoriler sayfası mı?

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookAdapter(Context context, List<Book> bookList, OnBookClickListener listener, boolean fromFavoritesPage) {
        this.context = context;
        this.bookList = bookList;
        this.listener = listener;
        this.fromFavoritesPage = fromFavoritesPage;
    }

    public void updateList(List<Book> newBooks) {
        this.bookList = newBooks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthors());

        Glide.with(context)
                .load(book.getThumbnail())
                .placeholder(R.drawable.ic_book_placeholder)
                .into(holder.imageViewCover);

        // Kalp ikonunu doldur/boş göster
        holder.btnFavorite.setImageResource(book.isFavorite() ?
                R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

        holder.btnFavorite.setOnClickListener(v -> {
            boolean isNowFavorite = !book.isFavorite();
            book.setFavorite(isNowFavorite);

            holder.btnFavorite.setImageResource(isNowFavorite ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference favRef = FirebaseDatabase.getInstance()
                        .getReference("Favorites")
                        .child(user.getUid())
                        .child(book.getId());

                if (isNowFavorite) {
                    favRef.setValue(book);
                } else {
                    favRef.removeValue();

                    // Favoriler sayfasında isek, listeden de sil
                    if (fromFavoritesPage) {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            bookList.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCover;
        TextView textViewTitle, textViewAuthor;
        ImageButton btnFavorite;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.imageViewCover);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
