using System.Collections.Generic;

namespace App
{
    /*
     * Manages a room, namely the set of contained clients.
     * Thread-safe.
     */
    public class Room
    {
        private readonly ISet<ConnectedClient> _clients = new HashSet<ConnectedClient>();
        private readonly object _lock = new ();

        public Room(string name)
        {
            Name = name;
        }

        public string Name { get; }

        public void Enter(ConnectedClient client)
        {
            lock (_lock)
            {
                _clients.Add(client);
            }
        }

        public void Leave(ConnectedClient client)
        {
            lock (_lock)
            {
                _clients.Remove(client);
            }
        }

        public void Post(ConnectedClient client, string message)
        {
            var formattedMessage = $"[{Name}]{client.Name} says '{message}'";
            lock (_lock)
            {
                foreach (var receiver in _clients)
                {
                    if (receiver != client)
                    {
                        receiver.PostRoomMessage(formattedMessage, this);
                    }
                }
            }
        }
    }
}