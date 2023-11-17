package com.sublinks.sublinksapi.private_messages.services;

import com.sublinks.sublinksapi.instance.models.LocalInstanceContext;
import com.sublinks.sublinksapi.private_messages.dto.PrivateMessage;
import com.sublinks.sublinksapi.private_messages.events.PrivateMessageCreatedPublisher;
import com.sublinks.sublinksapi.private_messages.events.PrivateMessageUpdatedPublisher;
import com.sublinks.sublinksapi.private_messages.repositories.PrivateMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrivateMessageService {
    private final PrivateMessageRepository privateMessageRepository;
    private final PrivateMessageCreatedPublisher privateMessageCreatedPublisher;
    private final PrivateMessageUpdatedPublisher privateMessageUpdatedPublisher;
    private final LocalInstanceContext localInstanceContext;

    public String generateActivityPubId(final com.sublinks.sublinksapi.private_messages.dto.PrivateMessage privateMessage) {

        String domain = localInstanceContext.instance().getDomain();
        return String.format("%s/private_message/%d", domain, privateMessage.getId());
    }

    @Transactional
    public void createPrivateMessage(final PrivateMessage privateMessage) {

        privateMessage.setActivityPubId("");
        privateMessageRepository.save(privateMessage);
        privateMessage.setActivityPubId(this.generateActivityPubId(privateMessage));
        privateMessageRepository.save(privateMessage);

        privateMessageCreatedPublisher.publish(privateMessage);
    }

    @Transactional
    public void updatePrivateMessage(final PrivateMessage comment) {

        privateMessageRepository.save(comment);
        privateMessageUpdatedPublisher.publish(comment);
    }

    @Transactional
    public void deletePrivateMessage(final PrivateMessage privateMessage) {

        privateMessageRepository.delete(privateMessage);
    }
}