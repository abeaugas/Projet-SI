import pygatt

def get_device_rssi(device_address, min_rssi=-100):
    adapter = pygatt.GATTToolBackend()
    adapter.start()

    try:
        devices = adapter.scan(timeout=8)
        for device in devices:
            print(device.get("address"))
            if device.get("address") == device_address:
                rssi = device.get("rssi", 0)
                if rssi >= min_rssi:
                    return rssi
    finally:
        adapter.stop()

    return None

if __name__ == "__main__":
    rssi = get_device_rssi('48:02:86:7E:E2:EA')  # Replace with your device's address
    if rssi is not None:
        print(f"RSSI: {rssi} dBm")
    else:
        print("Device not found or RSSI is below the minimum.")