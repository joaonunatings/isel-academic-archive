using System.Threading.Tasks;

namespace App{
    public class RequestWithoutCancel : TaskCompletionSource<bool>{
        public RequestWithoutCancel()
            // To ensure that completing the task doesn't run the continuations asynchronously.
            : base(TaskCreationOptions.RunContinuationsAsynchronously){
        }
    }
}