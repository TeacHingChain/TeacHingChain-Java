package com.thc.blockchain.util.addresses;

import java.util.ArrayList;
import java.util.List;

public class AddressBook {

    public AddressBook() { initAddressBook(); }

    public static List<String> addressBook;

    private static void initAddressBook() { addressBook = new ArrayList<>(); }
}
