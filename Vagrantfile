# -*- mode: ruby -*-
# vi: set ft=ruby :

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure(2) do |config|

  config.vm.define "ubuntu" do |ubuntu|
    config.vm.box = "ubuntu/trusty64"
    config.vm.network "forwarded_port", guest: 9898, host: 9898, protocol: 'udp', auto_correct: true
    config.vm.network "forwarded_port", guest: 9898, host: 9898, protocol: 'tcp', auto_correct: true
    config.vm.provision :shell, path: "scripts/vagrant/setup-ubuntu.sh"
    config.vm.post_up_message = "Apache Wave Dev environment setup - dist in /opt/apache/wave"

    config.vm.provider "virtualbox" do |v|
      v.name = "Apache Wave dev - Ubuntu"
      v.memory = 2048
      v.cpus = 1
    end
  end

  config.vm.define "fedora" do |fedora|
    config.vm.box = "fedora/23-cloud-base"
    config.vm.network "forwarded_port", guest: 9898, host: 9898, protocol: 'udp', auto_correct: true
    config.vm.network "forwarded_port", guest: 9898, host: 9898, protocol: 'tcp', auto_correct: true
    config.vm.provision :shell, path: "scripts/vagrant/setup-fedora.sh"
    config.vm.post_up_message = "Apache Wave Dev environment setup - dist in /opt/apache/wave"

    config.vm.provider "virtualbox" do |v|
      v.name = "Apache Wave dev - Fedora"
      v.memory = 2048
      v.cpus = 1
    end
  end

  #config.vm.define "win7" do |win7|
  #  config.vm.box = "modernIE/w7-ie11"

  #  config.vm.provider "virtualbox" do |vb|
  #    vb.name = "Apache Wave dev - Win7"
  #    vb.customize ["modifyvm", :id, "--memory", "3064"]
  #    vb.customize ["modifyvm", :id, "--vram", "128"]
  #    vb.customize ["modifyvm", :id,  "--cpus", "2"]
  #    vb.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  #    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
  #    vb.customize ["guestproperty", "set", :id, "/VirtualBox/GuestAdd/VBoxService/--timesync-set-threshold", 10000]
  #  end
  #end

  #config.vm.define "win8" do |win8|
  #  config.vm.box = "modernIE/w8.1-ie11"

  #  config.vm.provider "virtualbox" do |vb|
  #    vb.name = "Apache Wave dev - Win8.1"
  #    vb.customize ["modifyvm", :id, "--memory", "3064"]
  #    vb.customize ["modifyvm", :id, "--vram", "128"]
  #    vb.customize ["modifyvm", :id,  "--cpus", "2"]
  #    vb.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  #    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
  #    vb.customize ["guestproperty", "set", :id, "/VirtualBox/GuestAdd/VBoxService/--timesync-set-threshold", 10000]
  #  end
  #end

  #config.vm.define "win10" do |win10|
  #  config.vm.box = "modernIE/w10-edge"

  #  config.vm.provider "virtualbox" do |vb|
  #    vb.name = "Apache Wave dev - Win10"
  #    vb.customize ["modifyvm", :id, "--memory", "3064"]
  #    vb.customize ["modifyvm", :id, "--vram", "128"]
  #    vb.customize ["modifyvm", :id,  "--cpus", "2"]
  #    vb.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  #    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
  #    vb.customize ["guestproperty", "set", :id, "/VirtualBox/GuestAdd/VBoxService/--timesync-set-threshold", 10000]
  #  end
  #end
end
