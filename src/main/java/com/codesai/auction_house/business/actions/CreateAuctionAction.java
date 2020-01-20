package com.codesai.auction_house.business.actions;

import com.codesai.auction_house.business.auction.Auction;
import com.codesai.auction_house.business.auction.AuctionRepository;
import com.codesai.auction_house.business.auction.Item;

import static com.codesai.auction_house.business.generic.Money.money;

public class CreateAuctionAction {
    private AuctionRepository repository;

    public CreateAuctionAction(AuctionRepository repository) {
        this.repository = repository;
    }

    public String execute(CreateAuctionCommand command) {
        final var auction = new Auction(
                Item.item(command.name, command.description),
                money(command.initialBid),
                money(command.conquerPrice),
                command.expirationDate,
                money(command.minimumOverbiddingPrice)
        );
        repository.save(auction);
        return auction.id;
    }
}
