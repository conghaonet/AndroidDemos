package com.app2m.demo.mymp4parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

/**
 * Created by Administrator on 2015/5/30.
 */
public class ClipUtil {
	private static final String TAG = ClipUtil.class.getName();

	public static long clipVideoAudio(String videoFile, String audioFile, String outputFile, double begin, double end) throws Exception {
/*
		File mSdCardDir = Environment.getExternalStorageDirectory();
		File f = new File(mSdCardDir.getAbsolutePath());
		if (!f.exists()) {
			f.mkdir();
		}
*/
		long start0 = System.currentTimeMillis();
		List<Track> tracks = new ArrayList<>();
		Movie movieVideo = MovieCreator.build(videoFile);

/*
		MP3TrackImpl mp3Track = new MP3TrackImpl(new FileDataSourceImpl(audioFile));
		CroppedTrack ct=new CroppedTrack(mp3Track, 10, 100);
		for(Track track: movieVideo.getTracks()) {
			if(track.getHandler().equals("soun")) {
				tracks.remove(track);
			}
		}
		movieVideo.addTrack(ct);
		Container mp4file = new DefaultMp4Builder().build(movieVideo);
		FileChannel fileChannel = new FileOutputStream(outputFile).getChannel();
		mp4file.writeContainer(fileChannel);
		fileChannel.close();
		if (true)return 99;
*/



		for(Track track: movieVideo.getTracks()) {
			if(track.getHandler().equals("vide")) {
				tracks.add(track);
				break;
			}
		}
		Movie movieAudio = MovieCreator.build(audioFile);
		for(Track track: movieAudio.getTracks()) {
			if(track.getHandler().equals("soun")) {
				tracks.add(track);
				break;
			}
		}
		movieVideo.setTracks(new LinkedList<Track>());
		// remove all tracks we will create new tracks from the old

		double startTime1 = begin;
		double endTime1 = end;
		// double startTime2 = 30;
		// double endTime2 = 40;

		boolean timeCorrected = false;

		// Here we try to find a track that has sync samples. Since we can only
		// start decoding
		// at such a sample we SHOULD make sure that the start of the new
		// fragment is exactly
		// such a frame
		for (Track track : tracks) {
			if (track.getSyncSamples() != null
					&& track.getSyncSamples().length > 0) {
				if (timeCorrected) {
					// This exception here could be a false positive in case we
					// have multiple tracks
					// with sync samples at exactly the same positions. E.g. a
					// single movie containing
					// multiple qualities of the same video (Microsoft Smooth
					// Streaming file)
					Log.e(TAG,
							"The startTime has already been corrected by another track with SyncSample. Not Supported.");
					throw new RuntimeException(
							"The startTime has already been corrected by another track with SyncSample. Not Supported.");
				}
				startTime1 = correctTimeToSyncSample(track, startTime1, false);
				endTime1 = correctTimeToSyncSample(track, endTime1, true);
				// startTime2 = correctTimeToSyncSample(track, startTime2,
				// false);
				// endTime2 = correctTimeToSyncSample(track, endTime2, true);
				timeCorrected = true;
			}
		}

		int conghaoI = 0;
		for (Track track : tracks) {
//			if(conghaoI++ == 0) continue;
			long currentSample = 0;
			double currentTime = 0;
			double lastTime = 0;
			long startSample1 = -1;
			long endSample1 = -1;
			// long startSample2 = -1;
			// long endSample2 = -1;

			for (int i = 0; i < track.getSampleDurations().length; i++) {
				long delta = track.getSampleDurations()[i];

				if (currentTime > lastTime && currentTime <= startTime1) {
					// current sample is still before the new starttime
					startSample1 = currentSample;
				}
				if (currentTime > lastTime && currentTime <= endTime1) {
					// current sample is after the new start time and still
					// before the new endtime
					endSample1 = currentSample;
				}
				// if (currentTime > lastTime && currentTime <= startTime2) {
				// // current sample is still before the new starttime
				// startSample2 = currentSample;
				// }
				// if (currentTime > lastTime && currentTime <= endTime2) {
				// // current sample is after the new start time and still
				// before the new endtime
				// endSample2 = currentSample;
				// }
				lastTime = currentTime;
				currentTime += (double) delta
						/ (double) track.getTrackMetaData().getTimescale();
				currentSample++;
			}
			if (startSample1 < 0) startSample1 = 0;
			movieVideo.addTrack(new CroppedTrack(track, startSample1, endSample1));// new
			// AppendTrack(new
			// CroppedTrack(track,
			// startSample1,
			// endSample1),
			// new
			// CroppedTrack(track,
			// startSample2,
			// endSample2)));
		}
		long start1 = System.currentTimeMillis();
		Container out = new DefaultMp4Builder().build(movieVideo);
		long start2 = System.currentTimeMillis();
//		FileOutputStream fos = new FileOutputStream(f.getAbsolutePath()	+ File.separator + String.format("output-%f-%f.mp4", startTime1, endTime1));
		String fileSuffix = outputFile.substring(outputFile.lastIndexOf("."), outputFile.length());
		File file = new File(outputFile);
		int fileIndex = 0;
		while(file.exists()) {
			outputFile = outputFile.substring(0, outputFile.lastIndexOf("."))+"_"+(++fileIndex)+""+fileSuffix;
			file = new File(outputFile);
		}
		FileOutputStream fos = new FileOutputStream(outputFile);
		FileChannel fc = fos.getChannel();
		out.writeContainer(fc);

		fc.close();
		fos.close();
		long start3 = System.currentTimeMillis();
		Log.d(TAG, "Building IsoFile took : " + (start2 - start1) + "ms");
		Log.d(TAG, "Writing IsoFile took : " + (start3 - start2) + "ms");
		Log.d(TAG,
				"Writing IsoFile speed : "
						+ (new File(String.format("output-%f-%f.mp4",
						startTime1, endTime1)).length()
						/ (start3 - start2) / 1000) + "MB/s");
		return start3 - start0;
	}

	/**
	 * 截取指定时间段的视频
	 *
	 * @param path  视频的路径
	 * @param begin 需要截取的开始时间
	 * @param end   截取的结束时间
	 * @throws IOException
	 */
	public static void clipVideo(String path, double begin, double end) throws IOException {
		File mSdCardDir = Environment.getExternalStorageDirectory();
		File f = new File(mSdCardDir.getAbsolutePath());
		if (!f.exists()) {
			f.mkdir();
		}
		// Movie movie = new MovieCreator().build(new
		// RandomAccessFile("/home/sannies/suckerpunch-distantplanet_h1080p/suckerpunch-distantplanet_h1080p.mov",
		// "r").getChannel());
		Movie movie = MovieCreator.build(path);

		List<Track> tracks = movie.getTracks();
		movie.setTracks(new LinkedList<Track>());
		// remove all tracks we will create new tracks from the old

		double startTime1 = begin;
		double endTime1 = end;
		// double startTime2 = 30;
		// double endTime2 = 40;

		boolean timeCorrected = false;

		// Here we try to find a track that has sync samples. Since we can only
		// start decoding
		// at such a sample we SHOULD make sure that the start of the new
		// fragment is exactly
		// such a frame
		for (Track track : tracks) {
			if (track.getSyncSamples() != null
					&& track.getSyncSamples().length > 0) {
				if (timeCorrected) {
					// This exception here could be a false positive in case we
					// have multiple tracks
					// with sync samples at exactly the same positions. E.g. a
					// single movie containing
					// multiple qualities of the same video (Microsoft Smooth
					// Streaming file)
					Log.e(TAG,
							"The startTime has already been corrected by another track with SyncSample. Not Supported.");
					throw new RuntimeException(
							"The startTime has already been corrected by another track with SyncSample. Not Supported.");
				}
				startTime1 = correctTimeToSyncSample(track, startTime1, false);
				endTime1 = correctTimeToSyncSample(track, endTime1, true);
				// startTime2 = correctTimeToSyncSample(track, startTime2,
				// false);
				// endTime2 = correctTimeToSyncSample(track, endTime2, true);
				timeCorrected = true;
			}
		}

		int conghaoI = 0;
		for (Track track : tracks) {
			if(conghaoI++ == 0) continue;
			long currentSample = 0;
			double currentTime = 0;
			double lastTime = 0;
			long startSample1 = -1;
			long endSample1 = -1;
			// long startSample2 = -1;
			// long endSample2 = -1;

			for (int i = 0; i < track.getSampleDurations().length; i++) {
				long delta = track.getSampleDurations()[i];

				if (currentTime > lastTime && currentTime <= startTime1) {
					// current sample is still before the new starttime
					startSample1 = currentSample;
				}
				if (currentTime > lastTime && currentTime <= endTime1) {
					// current sample is after the new start time and still
					// before the new endtime
					endSample1 = currentSample;
				}
				// if (currentTime > lastTime && currentTime <= startTime2) {
				// // current sample is still before the new starttime
				// startSample2 = currentSample;
				// }
				// if (currentTime > lastTime && currentTime <= endTime2) {
				// // current sample is after the new start time and still
				// before the new endtime
				// endSample2 = currentSample;
				// }
				lastTime = currentTime;
				currentTime += (double) delta
						/ (double) track.getTrackMetaData().getTimescale();
				currentSample++;
			}
			if (startSample1 < 0) startSample1 = 0;
			movie.addTrack(new CroppedTrack(track, startSample1, endSample1));// new
			// AppendTrack(new
			// CroppedTrack(track,
			// startSample1,
			// endSample1),
			// new
			// CroppedTrack(track,
			// startSample2,
			// endSample2)));
		}
		long start1 = System.currentTimeMillis();
		Container out = new DefaultMp4Builder().build(movie);
		long start2 = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream(f.getAbsolutePath()
				+ File.separator
				+ String.format("output-%f-%f.mp4", startTime1, endTime1));
		FileChannel fc = fos.getChannel();
		out.writeContainer(fc);

		fc.close();
		fos.close();
		long start3 = System.currentTimeMillis();
		Log.e(TAG, "Building IsoFile took : " + (start2 - start1) + "ms");
		Log.e(TAG, "Writing IsoFile took : " + (start3 - start2) + "ms");
		Log.e(TAG,
				"Writing IsoFile speed : "
						+ (new File(String.format("output-%f-%f.mp4",
						startTime1, endTime1)).length()
						/ (start3 - start2) / 1000) + "MB/s");
	}

	private static double correctTimeToSyncSample(Track track, double cutHere,
												  boolean next) {
		double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
		long currentSample = 0;
		double currentTime = 0;
		for (int i = 0; i < track.getSampleDurations().length; i++) {
			long delta = track.getSampleDurations()[i];

			if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
				// samples always start with 1 but we start with zero therefore
				// +1
				timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(),
						currentSample + 1)] = currentTime;
			}
			currentTime += (double) delta
					/ (double) track.getTrackMetaData().getTimescale();
			currentSample++;

		}
		double previous = 0;
		for (double timeOfSyncSample : timeOfSyncSamples) {
			if (timeOfSyncSample > cutHere) {
				if (next) {
					return timeOfSyncSample;
				} else {
					return previous;
				}
			}
			previous = timeOfSyncSample;
		}
		return timeOfSyncSamples[timeOfSyncSamples.length - 1];
	}

}