/**
 * 
 */
/**
 * @author mkarier
 *
 */
module JavaStreamVideo {
	exports server;
	exports shared_class;
	exports client;

	requires com.sun.jna;
	requires java.desktop;
	requires uk.co.caprica.vlcj;
	requires uk.co.caprica.vlcj.natives;
	requires uk.co.caprica.vlcj.javafx;
	requires javafx.base;
	requires javafx.controls;
	requires transitive javafx.graphics;
	requires javafx.swing;
	requires javafx.media;
	requires org.controlsfx.controls;
}