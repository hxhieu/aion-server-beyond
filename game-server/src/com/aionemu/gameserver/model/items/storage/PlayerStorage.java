package com.aionemu.gameserver.model.items.storage;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * @author ATracer
 */
public class PlayerStorage extends Storage {

	private Player actor;

	public PlayerStorage(Player owner, StorageType storageType) {
		super(storageType);
		this.actor = owner;
	}

	@Override
	public final void setOwner(Player actor) {
		this.actor = actor;
	}

	@Override
	public void onLoadHandler(Item item) {
		if (item.isEquipped())
			actor.getEquipment().onLoadHandler(item);
		else {
			super.onLoadHandler(item);
		}
	}

	@Override
	public void increaseKinah(long amount) {
		increaseKinah(amount, actor);
	}

	@Override
	public void increaseKinah(long amount, ItemUpdateType updateType) {
		increaseKinah(amount, updateType, actor);
	}

	@Override
	public boolean tryDecreaseKinah(long amount) {
		return tryDecreaseKinah(amount, actor);
	}

	@Override
	public boolean tryDecreaseKinah(long amount, ItemUpdateType updateType) {
		return tryDecreaseKinah(amount, updateType, actor);
	}

	@Override
	public void decreaseKinah(long amount) {
		decreaseKinah(amount, actor);
	}

	@Override
	public void decreaseKinah(long amount, ItemUpdateType updateType) {
		decreaseKinah(amount, updateType, actor);
	}

	@Override
	public long increaseItemCount(Item item, long count) {
		return increaseItemCount(item, count, actor);
	}

	@Override
	public long increaseItemCount(Item item, long count, ItemUpdateType updateType) {
		return increaseItemCount(item, count, updateType, actor);
	}

	@Override
	public long decreaseItemCount(Item item, long count) {
		return decreaseItemCount(item, count, actor);
	}

	@Override
	public long decreaseItemCount(Item item, long count, ItemUpdateType updateType) {
		return decreaseItemCount(item, count, updateType, actor);
	}

	@Override
	public long decreaseItemCount(Item item, long count, ItemUpdateType updateType, QuestStatus questStatus) {
		return decreaseItemCount(item, count, updateType, questStatus, actor);
	}

	@Override
	public Item add(Item item) {
		return add(item, actor);
	}

	@Override
	public Item add(Item item, ItemAddType addType) {
		return add(item, addType, actor);
	}

	@Override
	public Item put(Item item) {
		return put(item, actor);
	}

	@Override
	public Item delete(Item item) {
		return delete(item, actor);
	}

	@Override
	public Item delete(Item item, ItemDeleteType deleteType) {
		return delete(item, deleteType, actor);
	}

	@Override
	public boolean decreaseByItemId(int itemId, long count) {
		return decreaseByItemId(itemId, count, actor);
	}

	@Override
	public boolean decreaseByItemId(int itemId, long count, QuestStatus questStatus) {
		return decreaseByItemId(itemId, count, questStatus, actor);
	}

	@Override
	public boolean decreaseByObjectId(int itemObjId, long count) {
		return decreaseByObjectId(itemObjId, count, actor);
	}

	@Override
	public boolean decreaseByObjectId(int itemObjId, long count, QuestStatus questStatus) {
		return decreaseByObjectId(itemObjId, count, questStatus, actor);
	}

	@Override
	public boolean decreaseByObjectId(int itemObjId, long count, ItemUpdateType updateType) {
		return decreaseByObjectId(itemObjId, count, updateType, actor);
	}

}
