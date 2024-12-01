package ru.rksp.rsocket.server;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FriendControllerTest {
    @Test
    public void testGetFriendById() {
        Friend friend = new Friend();
        friend.id = 1L;
        friend.name = "Nastya";

        FriendRepository friendRepository = Mockito.mock(FriendRepository.class);
        Mockito.when(friendRepository.findById(1L)).thenReturn(Mono.just(friend));

        FriendController friendController = new FriendController(friendRepository);

        ResponseEntity<Friend> response = friendController.getFriendById(1L).block();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(friend, response.getBody());
    }

    @Test
    public void testGetAllCats() {
        Friend friend1 = new Friend();
        friend1.id = 1L;
        friend1.name = "Nastya";

        Friend friend2 = new Friend();
        friend2.id = 1L;
        friend2.name = "Kira";

        FriendRepository friendRepository = Mockito.mock(FriendRepository.class);
        Mockito.when(friendRepository.findAll()).thenReturn(Flux.just(friend1, friend2));

        FriendController friendController = new FriendController(friendRepository);

        Flux<Friend> response = friendController.getAllFriends(null);
        Assertions.assertEquals(2, response.collectList().block().size());
    }

    @Test
    public void testCreateFriend() {
        Friend friend = new Friend();
        friend.id = 1L;
        friend.name = "Nastya";

        FriendRepository friendRepository = Mockito.mock(FriendRepository.class);
        Mockito.when(friendRepository.save(friend)).thenReturn(Mono.just(friend));

        FriendController friendController = new FriendController(friendRepository);

        Mono<Friend> response = friendController.createFriend(friend);
        Assertions.assertEquals(friend, response.block());
    }

    @Test
    public void testUpdateFriend() {
        Friend existingFriend = new Friend();
        existingFriend.id = 1L;
        existingFriend.name = "Nastya";

        Friend updatedFriend = new Friend();
        updatedFriend.id = 1L;
        updatedFriend.name = "Natusya";

        FriendRepository friendRepository = Mockito.mock(FriendRepository.class);
        Mockito.when(friendRepository.findById(1L)).thenReturn(Mono.just(existingFriend));
        Mockito.when(friendRepository.save(existingFriend)).thenReturn(Mono.just(updatedFriend));

        FriendController friendController = new FriendController(friendRepository);

        ResponseEntity<Friend> response = friendController.updateFriend(1L, updatedFriend).block();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(updatedFriend, response.getBody());
    }

    @Test
    public void testDeleteCat() {
        Friend friend = new Friend();
        friend.id = 1L;
        friend.name = "Nastya";

        Friend friend2 = new Friend();
        friend2.id = 1L;
        friend2.name = "Kira";

        FriendRepository friendRepository = Mockito.mock(FriendRepository.class);
        Mockito.when(friendRepository.findById(1L)).thenReturn(Mono.just(friend));
        Mockito.when(friendRepository.delete(friend)).thenReturn(Mono.empty());

        FriendController friendController = new FriendController(friendRepository);

        ResponseEntity<Void> response = friendController.deleteFriend(1L).block();
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
