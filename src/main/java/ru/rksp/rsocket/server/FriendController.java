package ru.rksp.rsocket.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Stream;

@Controller
public class FriendController {

    private final FriendRepository friendRepository;
    @Autowired
    public FriendController(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @GetMapping("/id")
    public Mono<ResponseEntity<Friend>> getFriendById(@PathVariable Long id) {
        return friendRepository.findById(id)
                .map(friend -> ResponseEntity.ok(friend))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Friend> getAllFriends(@RequestParam(name = "minage", required = false) Integer minAge) {
        Flux<Friend> friends = friendRepository.findAll();

        if(minAge != null && minAge > 0) {
            friends = friends.filter(friend -> friend.age >= minAge);
        }

        return friends
                .map(this::transformFriend)
                .onErrorResume(e -> Flux.error(new CustomException("Failed to fetch friends", e)))
                .onBackpressureBuffer();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Friend> createFriend(@RequestBody Friend friend) {
        return friendRepository.save(friend);
    }

    @PutMapping
    public Mono<ResponseEntity<Friend>> updateFriend(@PathVariable Long id,
                           @RequestBody Friend updatedFriend                          ) {
        return friendRepository.findById(id)
                .flatMap(existingFriend -> {
                    existingFriend.name = updatedFriend.name;
                    existingFriend.age = updatedFriend.age;
                    existingFriend.city = updatedFriend.city;
                    return friendRepository.save(existingFriend);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteFriend(@PathVariable Long id) {
        return friendRepository.findById(id)
                .flatMap(existingFriend ->
                    friendRepository.delete(existingFriend)
                            .then(Mono.just(
                                    new ResponseEntity<Void>(HttpStatus.NO_CONTENT)
                            )))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    private Friend transformFriend(Friend friend) {
        char[] chars = friend.name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i % 2 == 0) {
                chars[i] = Character.toUpperCase(chars[i]);
            } else {
                chars[i] = Character.toLowerCase(chars[i]);
            }
        }
        friend.name = new String(chars);
        return friend;
    }
}