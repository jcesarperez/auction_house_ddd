package unit.bussines.actions;

import com.codesai.auction_house.business.actions.CreateAuctionAction;
import com.codesai.auction_house.business.actions.CreateAuctionCommand;
import com.codesai.auction_house.business.auction.Auction;
import com.codesai.auction_house.business.auction.AuctionRepository;
import com.codesai.auction_house.business.auction.InitialBidIsGreaterThanConquerPrice;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static com.codesai.auction_house.business.generic.Money.*;
import static helpers.builder.AuctionBuilder.anAuction;
import static matchers.AuctionAssert.assertAuction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CreateAuctionActionShould {

    AuctionRepository auctionRepository = mock(AuctionRepository.class);
    ArgumentCaptor<Auction> captor = ArgumentCaptor.forClass(Auction.class);
    CreateAuctionAction action = new CreateAuctionAction(auctionRepository);

    @Test public void
    create_an_auction() {
        var expectedAuction = anAuction().build();
        var createAuctionCommand = commandFrom(expectedAuction);

        var actualId = action.execute(createAuctionCommand);

        verify(auctionRepository, times(1)).save(captor.capture());
        assertThat(actualId).isEqualTo(captor.getValue().id);
        assertAuction(captor.getValue()).isEqualTo(expectedAuction);
    }

    @Test public void
    not_create_an_auction_when_conquer_price_is_greater_than_initial_bid() {
        var auction = anAuction()
                        .setConquerPrice(money(10f))
                        .setInitialBid(money(11f))
                        .build();
        CreateAuctionCommand command = commandFrom(auction);

        assertThatThrownBy(() -> action.execute(command))
                .isInstanceOf(InitialBidIsGreaterThanConquerPrice.class);
    }

    private CreateAuctionCommand commandFrom(Auction expectedAuction) {
        return new CreateAuctionCommand(
                expectedAuction.item.name,
                expectedAuction.item.description,
                expectedAuction.initialBid.amount,
                expectedAuction.conquerPrice.amount,
                expectedAuction.expirationDate,
                expectedAuction.minimumOverbiddingPrice.amount
        );
    }

}
