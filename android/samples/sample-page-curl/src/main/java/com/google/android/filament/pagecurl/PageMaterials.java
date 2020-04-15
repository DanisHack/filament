/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.filament.pagecurl;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.filament.Engine;
import com.google.android.filament.Material;
import com.google.android.filament.MaterialInstance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PageMaterials {
    private final Material mMaterial;

    public enum Parameter {
        IMAGE_TEXTURE,
        APEX_FLOAT,
        THETA_FLOAT,
    }

    static String getParameterName(Parameter parameter) {
        switch (parameter) {
            case IMAGE_TEXTURE:
                return "imageTexture";
            case APEX_FLOAT:
                return "apexFloat";
            case THETA_FLOAT:
                return "thetaFloat";
        }
        return "";
    }

    public PageMaterials(Engine engine, AssetManager assets) {
        ByteBuffer asset = readAsset(assets, "materials/lit.filamat");
        assert asset != null;
        mMaterial = new Material.Builder()
                .payload(asset, asset.remaining())
                .build(engine);
    }

    public MaterialInstance createInstance() {
        MaterialInstance mi = mMaterial.createInstance();
        mi.setParameter("baseColor", 1.0f, 1.0f, 1.0f);
        mi.setParameter("roughness", 0.0f);
        mi.setParameter("metallic", 0.0f);
        return mi;
    }

    public Material getMaterial() {
        return mMaterial;
    }

    @Nullable
    @SuppressWarnings("SameParameterValue")
    private ByteBuffer readAsset(AssetManager assets, @NonNull String assetName) {
        ByteBuffer dst = null;
        try (AssetFileDescriptor fd = assets.openFd(assetName)) {
            InputStream in = fd.createInputStream();
            dst = ByteBuffer.allocate((int) fd.getLength());
            final ReadableByteChannel src = Channels.newChannel(in);
            src.read(dst);
            src.close();
            dst.rewind();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dst;
    }
}