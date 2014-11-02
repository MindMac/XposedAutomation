XposedAutomation
================

A demo to show how to install Xposed and enable Xposed based module automatically
(It seems the reboot process on the emulator has problems)

Content
------------

- AutomaticXposed: The application implemented to automate all the Xposed related procedures
- XposedInstaller: The modified [XposedInstaller](https://github.com/rovo89/XposedInstaller)(Added a **InstallService**)
- 
How to use
------------
Install and launcher AutomaticXposed application, the app will first installs XposedInstaller and then automatically enables the module itself and reboots(you need to wait about 30s after Xposed installed).

The installed XposedInstaller has been modified( refer to the XposedInstaller.zip), so remember to uninstall the official XposedInstaller before launcher AutomaticXposed.
