package com.threepm.api.toko.Service;

import com.threepm.api.toko.Model.Request.LoginRequest;
import com.threepm.api.toko.Model.Response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}