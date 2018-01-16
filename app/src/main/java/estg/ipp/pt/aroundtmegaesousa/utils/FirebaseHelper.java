package estg.ipp.pt.aroundtmegaesousa.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.internal.FirebaseAppHelper;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import estg.ipp.pt.aroundtmegaesousa.R;
import estg.ipp.pt.aroundtmegaesousa.activities.BaseActivity;
import estg.ipp.pt.aroundtmegaesousa.activities.RandomActivity;
import estg.ipp.pt.aroundtmegaesousa.fragments.PointOfInterestFragment;
import estg.ipp.pt.aroundtmegaesousa.interfaces.FirebaseServiceCommunication;
import estg.ipp.pt.aroundtmegaesousa.models.Favorite;
import estg.ipp.pt.aroundtmegaesousa.models.PointOfInterest;
import estg.ipp.pt.aroundtmegaesousa.models.Rating;
import estg.ipp.pt.aroundtmegaesousa.services.UploadFirebaseService;

/**
 * Created by José Bernardes on 11/01/2018.
 */

public class FirebaseHelper {

    public static final String TAG = UploadFirebaseService.TAG;
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAIL_UPLOAD_IMAGES = 2;
    public static final int RESULT_FAIL_ADD_DATABASE = 3;
    public static final String POINTS_COLLECTION = "points";
    public static final String PHOTOS_DIRECTORY = "photos";
    public static final float PHOTOS_PERCENTAGE = 0.75f;
    public static final float DATABASE_PERCENTAGE = 0.25f;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private CollectionReference points;
    private FirebaseServiceCommunication mListener;
    private List<String> photosURL;
    private List<String> photosThumbURL;
    private float photosProgressSteps;
    private double onGoingProgress;
    private double[] lastProgressPhotos;
    private double[] lastProgressThumbs;


    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
        points = db.collection(POINTS_COLLECTION);
    }


    /*
    points.document(3##).coleecitn(""rating).add(RATING)
     */
    public FirebaseHelper(FirebaseServiceCommunication mListener) {
        this.mListener = mListener;
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(
                new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(false)
                        .build());
        this.storage = FirebaseStorage.getInstance();
        points = db.collection(POINTS_COLLECTION);

    }

    /**
     * Add a point and their photos to firebase
     *
     * @param pointOfInterest
     * @param photos
     */
    public void addPointToFirebase(PointOfInterest pointOfInterest, List<File> photos) {
        List<List<byte[]>> list = getListOfBitmapsAsBytes(photos);
        photosProgressSteps = PHOTOS_PERCENTAGE / (photos.size() * 2);

        this.photosURL = new ArrayList<>();
        this.photosThumbURL = new ArrayList<>();
        lastProgressPhotos = new double[photos.size()];
        lastProgressThumbs = new double[photos.size()];
        addImagesToStorage(list.get(0), list.get(1), pointOfInterest);
    }


    private void addPointToDatabase(PointOfInterest point) {
        Log.d(TAG, "addPointToDatabase: ");
        points.add(point).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    mListener.updateProgressNotification((onGoingProgress * photosProgressSteps) + (100 * DATABASE_PERCENTAGE));
                    DocumentReference documentReference = task.getResult();
                    mListener.createResultNotification(true, documentReference.getId(), RESULT_SUCCESS);
                } else {
                    mListener.createResultNotification(false, null, RESULT_FAIL_ADD_DATABASE);
                }
            }
        });
    }


    private void addImagesToStorage(final List<byte[]> photos, final List<byte[]> photosThumbs, final PointOfInterest pointOfInterest) {
        Log.d(TAG, "addImagesToStorage: ");
        addImages(photos, pointOfInterest, photosURL, lastProgressPhotos, "PHOTOS");//photos
        addImages(photosThumbs, pointOfInterest, photosThumbURL, lastProgressThumbs, "THUMBS");//thumbs
    }


    private void addImages(final List<byte[]> photos, final PointOfInterest pointOfInterest, final List<String> urlList, final double[] lastProgress, final String type) {
        for (int i = 0; i < photos.size(); i++) {
            final int position = i;
            String uID = UUID.randomUUID().toString();
            String path = FirebaseHelper.PHOTOS_DIRECTORY + "/" + uID + ".jpeg";
            StorageReference photoRef = storage.getReference(path);
            UploadTask uploadTask = photoRef.putBytes(photos.get(i));
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        synchronized (photosURL) {
                            synchronized (photosThumbURL) {
                                urlList.add(task.getResult().getDownloadUrl().toString());
                                if (photosURL.size() == photos.size() && photosThumbURL.size() == photos.size()) { //se já fez upload de todas as fotos
                                    pointOfInterest.setPhotos(photosURL);
                                    pointOfInterest.setPhotosThumbs(photosThumbURL);
                                    pointOfInterest.setDate(Calendar.getInstance().getTime());
                                    addPointToDatabase(pointOfInterest);
                                }
                            }
                        }
                    } else {
                        mListener.createResultNotification(false, null, FirebaseHelper.RESULT_FAIL_UPLOAD_IMAGES);
                    }
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double actual = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    onGoingProgress += actual - lastProgress[position]; //o que vem menos o anterior
                    lastProgress[position] = actual;
                    Log.d(TAG, "P: " + position + "T: " + type + " oG: " + onGoingProgress);
                    mListener.updateProgressNotification((onGoingProgress * photosProgressSteps));
                    Log.d(TAG, "% " + (onGoingProgress * photosProgressSteps));
                }
            });
        }


    }

    private List<List<byte[]>> getListOfBitmapsAsBytes(List<File> files) {
        List<byte[]> photos = new ArrayList<>();
        List<byte[]> photoThumbs = new ArrayList<>();
        for (File file : files) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmap, 256, 256);
            photos.add(convertBitmapToByte(bitmap));
            photoThumbs.add(convertBitmapToByte(thumb));
        }
        List<List<byte[]>> list = new ArrayList<>();
        list.add(photos);
        list.add(photoThumbs);
        return list;
    }


    private byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public void deletePOI(String id, final PointOfInterestFragment context) {
        points.document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        context.deleteSuccess(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        context.deleteSuccess(false);
                    }
                });
    }


    public void addRating(Rating rating, String idPoi, final PointOfInterestFragment context) {
        points.document(idPoi).collection("ratings").add(rating).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    context.addRatingSuccess();
                } else {
                    context.addRatingSuccess();
                }
            }
        });
    }


    public void checkRating(String idPoi, String user, final PointOfInterestFragment context) {
        Query query = points.document(idPoi).collection("ratings").whereEqualTo("id", user);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Rating rating;
                if (task.getResult().getDocuments().isEmpty()) {
                    rating = null;
                } else {
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    rating = documentSnapshot.toObject(Rating.class);
                    rating.setId(documentSnapshot.getId());
                }
                context.existRating(rating);
            }
        });

    }

    public void editRating(String idPoi, String ratingID, float rate, final PointOfInterestFragment context) {
        DocumentReference rating = points.document(idPoi).collection("ratings").document(ratingID);
        rating.update("rating", rate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    context.editRatingSuccess();
                } else {
                    context.editRatingUnsuccess();
                }
            }
        });
    }

    public void addFavorite(Favorite fav, String idPOI, final PointOfInterestFragment context) {
        points.document(idPOI).collection("favorites").add(fav).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    context.addFavoritesSucess();
                } else {
                    context.addFavoritesUnSucess();
                }
            }
        });
    }

    public void checkFavorites(String idPoi, String user, final PointOfInterestFragment context) {
        Query query = points.document(idPoi).collection("favorites").whereEqualTo("idUser", user);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Favorite favorite;
                if (task.getResult().getDocuments().isEmpty()) {
                    favorite = null;
                } else {
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    favorite = documentSnapshot.toObject(Favorite.class);
                    favorite.setIdFavorites(documentSnapshot.getId());
                }
                context.existFavorite(favorite);
            }
        });
    }

    public void removeFavorite(String idPOI, String idFav, final PointOfInterestFragment context) {
        DocumentReference favorites = points.document(idPOI).collection("favorites").document(idFav);
        favorites.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    context.removeFavoritesSuccess();
                } else {
                    context.removeFavoritesUnSuccess();
                }
            }
        });
    }


}
          /*  notificationUtils.showNotify();*/
/*            //Thumbnail
            String thumbPath = FirebaseHelper.PHOTOS_DIRECTORY + uID + "_thumb.jpeg";
            StorageReference thumbRef = storage.getReference(thumbPath);
            UploadTask uploadThumbTask = photoRef.putBytes(images.get(1));
            uploadThumbTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        notificationUtils.updateStatus(inc);
                        synchronized (photosURL) {
                            photosThumbURL.add(task.getResult().getDownloadUrl().toString());
                            if (photosURL.size() == photos.size()) { //se já fez upload de todas as fotos
                                pointOfInterest.setPhotos(photosURL);
                                pointOfInterest.setDate(Calendar.getInstance().getTime());
                                notificationUtils.finishStatus();
                                new FirebaseHelper(context).addPointToDatabase(pointOfInterest);
                            }
                        }
                    } else {
                        context.addPointResult(false, null, FirebaseHelper.RESULT_FAIL_UPLOAD_IMAGES);
                    }
                }
            });*/
