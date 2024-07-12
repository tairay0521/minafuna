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
import app.ManagerFactory;
import app.checkin.CheckInRoomForm;
import app.checkout.CheckOutRoomForm;
import app.reservation.ReserveRoomControl;
import app.reservation.ReserveRoomForm;
import domain.DaoFactory;
import domain.room.AvailableQty;

/**
 * CUI class for Hotel Reservation Systems
 * 
 */
public class CUI {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final int ADMINISTRATOR_PASSWORD = "YAMAZAKI_TAIRON".hashCode();
	private BufferedReader reader;

	private int profit;

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
				System.out.println("3: Check reservation");
				System.out.println("4: Change reservation");
				System.out.println("5: Cancel reservation");
				System.out.println("9: End");
				System.out.print("> ");

				try {
					String menu = reader.readLine();
					selectMenu = Integer.parseInt(menu);
				} catch (NumberFormatException e) {
					selectMenu = 9;
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
					case 3:
						checkReservation();
						break;
					case 4:
						changeReservation();
						break;
					case 5:
						cancelReservation();
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
				System.out.println("3. Check profit");
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
						profit += 20000;
						break;
					case 3:
						System.out.println("Profit: " + profit);
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
		var qtyDao = DaoFactory.getInstance().getAvailableQtyDao();
		var rm = ManagerFactory.getInstance().getRoomManager();
		try {
			System.out.println("Checking empty rooms");
			System.out.println("Input date in the form of yyyy/mm/dd");
			System.out.print("> ");
			String dateStr = reader.readLine();
			Date date = DateUtil.convertToDate(dateStr);
			if (date == null) {
				System.out.println("Invalid input");
				return;
			}
			var emptyList = qtyDao.getAvailableQty(date);
			var qty = (emptyList == null ||
					emptyList.getQty() == AvailableQty.AVAILABLE_ALL) ? rm.getMaxAvailableQty() : emptyList.getQty();
			System.out.println("Number of empty rooms: " + qty);
		} catch (Exception e) {
			System.err.println("Error" + e.getMessage());
			throw new AppException("Failed to check empty rooms", e);
		}
	}

	private void checkReservation() throws AppException {
		var reservationDAO = DaoFactory.getInstance().getReservationDao();
		try {
			System.out.println("Checking your reservation");
			System.out.println("Input reservation number");
			System.out.print("> ");
			String reservationNumber = reader.readLine();
			var reservation = reservationDAO.getReservation(reservationNumber);
			if (reservation == null) {
				System.out.println("Reservation not found");
				return;
			} else {
				System.out.println("Reservation found");
				System.out.println("Reservation number: " + reservation.getReservationNumber());
				System.out.println("Staying date: " + DateUtil.convertToString(reservation.getStayingDate()));
				System.out.println("Roomtype: " + reservation.getStatus());
			}
		} catch (Exception e) {
			throw new AppException("Failed to check reservations", e);
		}

	}

	private void cancelReservation() throws IOException, AppException {
		System.out.println("Canceling your reservation");
		System.out.println("Input reservation number");
		System.out.print("> ");

		String reservationNumber = reader.readLine();
		var ctl = new ReserveRoomControl();
		ctl.removeReservation(reservationNumber);
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

	private void changeReservation() throws IOException, AppException {
		var reservationDAO = DaoFactory.getInstance().getReservationDao();

		try {
			System.out.println("Change your reservation");
			System.out.println("Input reservation number");
			System.out.print("> ");
			String oldReservationNumber = reader.readLine();
			var reservation = reservationDAO.getReservation(oldReservationNumber);
			if (reservation == null) {
				System.out.println("Reservation not found");
				return;
			} else {
				reservationDAO.deleteReservation(oldReservationNumber);
				System.out.println("Reservation found");
			}
		} catch (Exception e) {
			throw new AppException("Failed to find reservation", e);
		}

		System.out.println("Input new arrival date in the form of yyyy/mm/dd");
		System.out.print("> ");

		String dateStr = reader.readLine();
		Date stayingDate = DateUtil.convertToDate(dateStr);

		if (stayingDate == null) {
			System.out.println("Invalid input");
			return;
		}

		ReserveRoomForm reserveRoomForm = new ReserveRoomForm();
		reserveRoomForm.setStayingDate(stayingDate);
		String newReservationNumber = reserveRoomForm.submitReservation();

		System.out.println("Reservation change has been completed.");
		System.out.println("New arrival (staying) date is " + DateUtil.convertToString(stayingDate) + ".");
		System.out.println("New reservation number is " + newReservationNumber + ".");
	}

	public static void main(String[] args) throws Exception {
		CUI cui = new CUI();
		cui.execute();
	}
}
