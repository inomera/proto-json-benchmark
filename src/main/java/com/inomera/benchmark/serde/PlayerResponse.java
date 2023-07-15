package com.inomera.benchmark.serde;

import java.io.Serializable;

public class PlayerResponse implements Serializable {

    private PlayerInfo playerInfo;
    private RestResponseStatus status;
    private String txKey;

    public PlayerResponse() {
    }

    public PlayerResponse(PlayerInfo playerInfo, RestResponseStatus status, String txKey) {
	this.playerInfo = playerInfo;
	this.status = status;
	this.txKey = txKey;
    }

}

class PlayerInfo implements Serializable {
    private Long id;
    private String name;
    private String status;

    public PlayerInfo() {
    }

    public PlayerInfo(Long id, String name, String status) {
	this.id = id;
	this.name = name;
	this.status = status;
    }
}

class RestResponseStatus implements Serializable {
    private String code;
    private String description;

    public RestResponseStatus() {
    }

    public RestResponseStatus(String code, String description) {
	this.code = code;
	this.description = description;
    }
}
