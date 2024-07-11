/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package app.checkout;

import app.AppException;
import app.ManagerFactory;
import domain.payment.PaymentManager;
import domain.payment.PaymentException;
import domain.room.RoomManager;
import domain.room.RoomException;

/**
 * Control class for Check-out Customer
 */
public class CheckOutRoomControl {
	
	public void checkOut(String roomNumber) throws AppException {
		try {
			//Clear room
			var rm = getRoomManager();
			var date = rm.removeCustomer(roomNumber);
			//Consume payment
			var pm = getPaymentManager();
			pm.consumePayment(date, roomNumber);
		}
		catch (RoomException e) {
			AppException exception = new AppException("Failed to check-out", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
		catch (PaymentException e) {
			AppException exception = new AppException("Failed to check-out", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
	}

	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}

	private PaymentManager getPaymentManager() {
		return ManagerFactory.getInstance().getPaymentManager();
	}
}
