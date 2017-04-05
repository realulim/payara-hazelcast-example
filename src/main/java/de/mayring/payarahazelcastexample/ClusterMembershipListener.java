package de.mayring.payarahazelcastexample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import com.hazelcast.core.IMap;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

public class ClusterMembershipListener implements MembershipListener {

    private final List<String> allColors;
    private final String defaultColor = "LightSalmon";
    private final IMap<String, String> colorsInUse;

    public ClusterMembershipListener(IMap<String, String> colorsInUse) {
        allColors = Arrays.asList(new String[] { "Crimson", "LightSeaGreen", "Gold", "RoyalBlue", "Black" });
        this.colorsInUse = colorsInUse;
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        initialiseNewMember(membershipEvent.getMember().getUuid());
    }

    public void initialiseNewMember(String uuid) {
        String colorToAdd = getUnusedColor(colorsInUse);
        String assignedColor = colorsInUse.putIfAbsent(uuid, colorToAdd);
        if (assignedColor == null) {
            Logger.getAnonymousLogger().info("Color " + colorToAdd + " assigned to new Member " + uuid);
        }
        else {
            Logger.getAnonymousLogger().info("Color " + colorToAdd + " not assigned, Member " + uuid + " already had Color " + assignedColor);
        }
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        String uuidToRemove = membershipEvent.getMember().getUuid();
        colorsInUse.remove(uuidToRemove);
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        // System.err.println("Member attribute changed: " + memberAttributeEvent);
    }

    private String getUnusedColor(IMap<String, String> colorsInUse) {
        if (colorsInUse.size() >= allColors.size()) {
            // all predefined colors are taken, so for any overflow nodes let's use the default color
            return defaultColor;
        }
        else {
            // use a color that is not already taken
            List<String> colorsAvailable = new ArrayList<>(allColors);
            for (String color : colorsInUse.values()) {
                colorsAvailable.remove(color);
            }
            return colorsAvailable.get(ThreadLocalRandom.current().nextInt(0, colorsAvailable.size()));
        }
    }

}
