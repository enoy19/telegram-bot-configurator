/**
 * 
 */
package io.enoy.tbc.application.control;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.SendResponse;
import io.enoy.tbc.application.model.Role;
import io.enoy.tbc.application.model.User;
import io.enoy.tbc.application.model.input.UserInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

/**
 * @author Enis.Oezsoy
 */
public interface MessageSender {

	public static File TMP_DIR = new File("tbc/tmp/");

	public default Message send(Object msg) {
		return sendTo(getUser(), msg);
	}

	public default Message sendTo(User user, Object msg) {
		String userId = user.getId();
		if (user.isActivated()) {
			return sendTo(userId, msg);
		} else {
			return null;
		}
	}

	public default Message[] sendTo(User[] users, Object msg) {
		Message[] messages = new Message[users.length];

		for (int i = 0; i < users.length; i++) {
			messages[i] = sendTo(users[i], msg);
		}

		return messages;
	}

	// can also be used for chatId
	public default Message sendTo(String userId, Object msg) {
		String message = msg == null ? "null" : msg.toString();
		SendResponse response = execute(new SendMessage(userId, message));
		return response.message();
	}

	public default Message[] sendToRole(String roleName, Object msg) {
		return sendToRole(getRole(roleName), msg);
	}

	public default Message[] sendToRole(Role role, Object msg) {
		return sendTo(getUsersOfRole(role), msg);
	}

	public default <T extends BaseResponse> T execute(BaseRequest<?, T> request) {
		return getBot().execute(request);
	}

	public Role getRole(String roleName);

	public User[] getUsersOfRole(Role role);

	public TelegramBot getBot();

	public String getBotToken();

	public User getUser();

	public default File getFile(UserInput userInput) {
		// String fileId = getFileId(userInput);

		if (!TMP_DIR.exists()) {
			TMP_DIR.mkdirs();
			TMP_DIR.deleteOnExit();
		}

		try {
			File tmpFile = Files.createTempFile(//
					TMP_DIR.toPath(), //
					userInput.getType().name() + "_", //
					null).toFile();//
			tmpFile.deleteOnExit();

			return getFile(userInput, tmpFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public default File getFile(UserInput userInput, String savePath) {
		File file = new File(savePath);
		return getFile(userInput, file);
	}

	public default File getFile(UserInput userInput, File file) {
		String fileId = getFileId(userInput);

		return getFile(fileId, file);
	}

	public default String getFileId(UserInput userInput) {
		String fileId = null;

		switch (userInput.getType()) {
		case AUDIO:
			Audio audio = (Audio) userInput.getData();
			fileId = audio.fileId();
			break;
		case DOCUMENT:
			Document document = (Document) userInput.getData();
			fileId = document.fileId();
			break;
		case PHOTO:
			PhotoSize[] photo = (PhotoSize[]) userInput.getData();
			fileId = photo[photo.length - 1].fileId();
			break;
		case STICKER:
			Sticker sticker = (Sticker) userInput.getData();
			fileId = sticker.fileId();
			break;
		case VIDEO:
			Video video = (Video) userInput.getData();
			fileId = video.fileId();
			break;
		case VOICE:
			Voice voice = (Voice) userInput.getData();
			fileId = voice.fileId();
			break;
		default:
			break;
		}
		return fileId;
	}

	public default File getFile(String fileId, String savePath) {
		File file = new File(savePath);
		return getFile(fileId, file);
	}

	public default File getFile(String fileId, File file) {
		String downloadLink = getDownloadLink(fileId);

		URL website;
		try {
			website = new URL(downloadLink);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return file;
	}

	public default String getDownloadLink(String fileId) {
		GetFileResponse fileResponse = getBot().execute(new GetFile(fileId));
		com.pengrad.telegrambot.model.File file = fileResponse.file();

		String downloadLink = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.filePath();
		return downloadLink;
	}

	public default String getChatId() {
		return getUser().getId();
	}

	public default void update(Message msg, Object text) {
		String msgId = Integer.toString(msg.messageId());
		EditMessageText emt = new EditMessageText(msgId, text.toString());
		emt.getParameters().put("message_id", msgId);
		emt.getParameters().put("chat_id", msg.chat().id());
		execute(emt);
	}

	public default void update(Message[] messages, Object text) {
		for (int i = 0; i < messages.length; i++) {
			update(messages[i], text);
		}
	}

	public default void document(File file) {
		execute(new SendDocument(getChatId(), file));
	}

	public default void document(Object path) {
		document(new File(path.toString().trim()));
	}

	public default void file(Object path) {
		// null!
		document(new File(path.toString().trim()));
	}

	public default void file(File file) {
		document(file);
	}

	public default void photo(File file) {
		execute(new SendPhoto(getChatId(), file));
	}

	public default void photo(Object path) {
		photo(new File(path.toString().trim()));
	}

	public default void photo(File file, String caption) {
		execute(new SendPhoto(getChatId(), file).caption(caption));
	}

	public default void photo(Object path, Object caption) {
		photo(new File(path.toString().trim()), caption.toString());
	}

	public default void audio(File file) {
		execute(new SendAudio(getChatId(), file));
	}

	public default void audio(Object path) {
		audio(new File(path.toString().trim()));
	}

	public default void video(File file) {
		execute(new SendVideo(getChatId(), file));
	}

	public default void video(Object path) {
		video(new File(path.toString().trim()));
	}

	public default void location(float latitude, float longitude) {
		execute(new SendLocation(getChatId(), latitude, longitude));
	}

	public default void location(Object latitude, Object longitude) {
		location(Float.parseFloat(latitude.toString()), Float.parseFloat(longitude.toString()));
	}

}
