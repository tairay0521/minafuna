/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package app.cui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import util.DateUtil;
import app.AppException;
import app.checkin.CheckInRoomForm;
import app.checkout.CheckOutRoomForm;
import app.reservation.ReserveRoomForm;
import domain.DaoFactory;

/**
 * CUI class for Hotel Reservation Systems
 * 
 */
public class CUI {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final int ADMINISTRATOR_PASSWORD = "YAMAZAKI_TAIRON".hashCode();
	private BufferedReader reader;

	CUI() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	private void execute() throws IOException {
		System.out.println("Hotel Reservation System ~~Synod-Minafuna Bay Hotel~~");

		System.out.println("1. Client");
		System.out.println("2. Administrator");

		System.out.print("> ");

		try {
			String input = reader.readLine();
			int select = Integer.parseInt(input);

			switch (select) {
				case 1:
					executeClient();
					break;
				case 2:
					System.out.println("Input password");
					System.out.print("> ");
					String password = reader.readLine();
					if (password.hashCode() != ADMINISTRATOR_PASSWORD) {
						System.out.println("Invalid password");
						break;
					}
					executeAdmin();
					break;
				default:
					System.out.println("Invalid input");
					break;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input");
		} finally {
			reader.close();
		}
		System.out.println("Terminated the system.");
	}

	private void executeClient() throws IOException {
		try {
			while (true) {
				int selectMenu;
				System.out.println("");
				System.out.println("Menu");
				System.out.println("1. Check empty rooms");
				System.out.println("2: Reservation");
				System.out.println("9: End");
				System.out.print("> ");

				try {
					String menu = reader.readLine();
					selectMenu = Integer.parseInt(menu);
				} catch (NumberFormatException e) {
					selectMenu = 4;
				}

				if (selectMenu == 9) {
					break;
				}

				switch (selectMenu) {
					case 1:
						checkEmptyRooms();
						break;
					case 2:
						reserveRoom();
						break;
				}
			}
			System.out.println("Ended");
		} catch (AppException e) {
			System.err.println("Error");
			System.err.println(e.getFormattedDetailMessages(LINE_SEPARATOR));
		}
	}

	private void executeAdmin() throws IOException {
		System.out.println("Administrator mode");

		try {
			while (true) {
				System.out.println("1. Check-in");
				System.out.println("2. Check-out");
				System.out.println("9. End");

				System.out.print("> ");
				String input = reader.readLine();
				int select = Integer.parseInt(input);

				switch (select) {
					case 1:
						checkInRoom();
						break;
					case 2:
						checkOutRoom();
						break;
					case 9:
						return;
					default:
						System.out.println("Invalid input");
						break;
				}
			}
		} catch (AppException e) {
			System.err.println("Error");
			System.err.println(e.getFormattedDetailMessages(LINE_SEPARATOR));
		}
	}

	private void checkEmptyRooms() throws AppException {
		var roomsDAO = DaoFactory.getInstance().getRoomDao();
		try {
			var emptyList = roomsDAO.getEmptyRooms();
			emptyList.sort((a, b) -> a.getRoomNumber().compareTo(b.getRoomNumber()));
			System.out.println("Empty rooms");
			for (var room : emptyList) {
				System.out.println("Room Number: " + room.getRoomNumber());
			}
		} catch (Exception e) {
			throw new AppException("Failed to check empty rooms", e);
		}
	}

	private void reserveRoom() throws IOException, AppException {
		System.out.println("Input arrival date in the form of yyyy/mm/dd");
		System.out.print("> ");

		String dateStr = reader.readLine();

		// Validate input
		Date stayingDate = DateUtil.convertToDate(dateStr);
		if (stayingDate == null) {
			System.out.println("Invalid input");
			return;
		}

		ReserveRoomForm reserveRoomForm = new ReserveRoomForm();
		reserveRoomForm.setStayingDate(stayingDate);
		String reservationNumber = reserveRoomForm.submitReservation();

		System.out.println("Reservation has been completed.");
		System.out.println("Arrival (staying) date is " + DateUtil.convertToString(stayingDate) + ".");
		System.out.println("Reservation number is " + reservationNumber + ".");
	}

	private void checkInRoom() throws IOException, AppException {
		System.out.println("Input reservation number");
		System.out.print("> ");

		String reservationNumber = reader.readLine();

		if (reservationNumber == null || reservationNumber.length() == 0) {
			System.out.println("Invalid reservation number");
			return;
		}

		CheckInRoomForm checkInRoomForm = new CheckInRoomForm();
		checkInRoomForm.setReservationNumber(reservationNumber);

		String roomNumber = checkInRoomForm.checkIn();
		System.out.println("Check-in has been completed.");
		System.out.println("Room number is " + roomNumber + ".");

	}

	private void checkOutRoom() throws IOException, AppException {
		System.out.println("Input room number");
		System.out.print("> ");

		String roomNumber = reader.readLine();

		if (roomNumber == null || roomNumber.length() == 0) {
			System.out.println("Invalid room number");
			return;
		}

		CheckOutRoomForm checkoutRoomForm = new CheckOutRoomForm();
		checkoutRoomForm.setRoomNumber(roomNumber);
		checkoutRoomForm.checkOut();
		System.out.println("Check-out has been completed.");
	}

	public static void main(String[] args) throws Exception {
		CUI cui = new CUI();
		cui.execute();
	}
}
