package testcam;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
public class cam {
	public static void main(String[] args) throws Exception {
			 
				OpenCVFrameGrabber frameGrabber = new OpenCVFrameGrabber("http://192.168.1.45:8080/mjpg/video.mjpg"); 
			    frameGrabber.setFormat("mjpeg");
			    frameGrabber.start();
			    IplImage iPimg = frameGrabber.grab();
			    CanvasFrame canvasFrame = new CanvasFrame("Camera");
			    canvasFrame.setCanvasSize(iPimg.width(), iPimg.height());
			    
			    while (canvasFrame.isVisible() && (iPimg = frameGrabber.grab()) != null) {
			        canvasFrame.showImage(iPimg);
			    }
			    frameGrabber.stop();
			    canvasFrame.dispose();
			    System.exit(0);
			}
	}