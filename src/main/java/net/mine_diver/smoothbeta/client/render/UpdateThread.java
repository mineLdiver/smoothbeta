package net.mine_diver.smoothbeta.client.render;

import java.util.LinkedList;
import java.util.List;

import net.mine_diver.smoothbeta.mixin.client.multidraw.TessellatorAccessor;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.chunk.ChunkBuilder;

public class UpdateThread extends Thread {
    public static final UpdateThread INSTANCE = new UpdateThread();
    static {
        INSTANCE.start();
    }

//  private Pbuffer pbuffer = null;
    private final Object lock = new Object();
    private final List<ChunkBuilder> updateList = new LinkedList<>();
    private final List<ChunkBuilder> updatedList = new LinkedList<>();
    private int updateCount = 0;
    private final Tessellator mainTessellator = Tessellator.INSTANCE;
    public final Tessellator threadTessellator = TessellatorAccessor.smoothbeta_create(262144);
    private boolean working = false;
    private ChunkBuilder currentBuilder = null;
    private boolean canWork = false;
    private boolean canWorkToEndOfUpdate = false;
    private static final int MAX_UPDATE_CAPACITY = 10;

    public UpdateThread() {
        super("UpdateThread");
//    this.pbuffer = pbuffer;
        setDaemon(true);
    }

    public void addBuilderToUpdate(ChunkBuilder builder, boolean first) {
        synchronized (this.lock) {
            if (((SmoothChunkBuilder) builder).smoothbeta_isUpdating())
                throw new IllegalArgumentException("Renderer already updating");
            if (first) {
                this.updateList.add(0, builder);
            } else {
                this.updateList.add(builder);
            }
            ((SmoothChunkBuilder) builder).smoothbeta_setUpdating(true);
            this.lock.notifyAll();
        }
    }

    private ChunkBuilder getBuilderToUpdate() {
        synchronized (this.lock) {
            while (this.updateList.isEmpty()) {
                try {
                    this.lock.wait();
                } catch (InterruptedException ignored) {}
            }
            this.currentBuilder = this.updateList.remove(0);
            this.lock.notifyAll();
            return this.currentBuilder;
        }
    }

    public boolean hasWorkToDo() {
        synchronized (this.lock) {
            if (!this.updateList.isEmpty())
                return true;
            if (this.currentBuilder != null)
                return true;
            return this.working;
        }
    }

    public int getUpdateCapacity() {
        synchronized (this.lock) {
            if (this.updateList.size() > MAX_UPDATE_CAPACITY)
                return 0;
            return MAX_UPDATE_CAPACITY - this.updateList.size();
        }
    }

    private void rendererUpdated(ChunkBuilder wr) {
        synchronized (this.lock) {
            this.updatedList.add(wr);
            this.updateCount++;
            this.currentBuilder = null;
            this.working = false;
            this.lock.notifyAll();
        }
    }

    private void finishUpdatedRenderers() {
        synchronized (this.lock) {
            for (int i = 0; i < this.updatedList.size(); i++) {
                ChunkBuilder wr = this.updatedList.get(i);
//        wr.finishUpdate();
                ((SmoothChunkBuilder) wr).smoothbeta_setUpdating(false);
            }
            this.updatedList.clear();
        }
    }

    public void run() {
//    try {
//      this.pbuffer.makeCurrent();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
        while (!Thread.interrupted()) {
            try {
                ChunkBuilder builder = getBuilderToUpdate();
                checkCanWork();
                try {
                    TessellatorAccessor.smoothbeta_setInstance(this.threadTessellator);
                    builder.rebuild();
                } finally {
                    TessellatorAccessor.smoothbeta_setInstance(this.mainTessellator);
                }
                rendererUpdated(builder);
            } catch (Exception e) {
                e.printStackTrace();
                if (this.currentBuilder != null) {
                    ((SmoothChunkBuilder) this.currentBuilder).smoothbeta_setUpdating(false);
                    this.currentBuilder.invalidated = true;
                }
                this.currentBuilder = null;
                this.working = false;
            }
        }
    }

    public void pause() {
        synchronized (this.lock) {
            this.canWork = false;
            this.canWorkToEndOfUpdate = false;
            this.lock.notifyAll();
            while (this.working) {
                try {
                    this.lock.wait();
                } catch (InterruptedException ignored) {
                }
            }
            finishUpdatedRenderers();
        }
    }

    public void unpause() {
        synchronized (this.lock) {
            if (this.working)
                System.out.println("UpdateThread still working in unpause()!!!");
            this.canWork = true;
            this.canWorkToEndOfUpdate = false;
            this.lock.notifyAll();
        }
    }

    public void unpauseToEndOfUpdate() {
        synchronized (this.lock) {
            if (this.working)
                System.out.println("UpdateThread still working in unpause()!!!");
            while (this.currentBuilder != null) {
                this.canWork = false;
                this.canWorkToEndOfUpdate = true;
                this.lock.notifyAll();
                try {
                    this.lock.wait();
                } catch (InterruptedException ignored) {
                }
            }
            pause();
        }
    }

    private void checkCanWork() {
        Thread.yield();
        synchronized (this.lock) {
            while (!this.canWork) {
                if (this.canWorkToEndOfUpdate && this.currentBuilder != null)
                    break;
                this.working = false;
                this.lock.notifyAll();
                try {
                    this.lock.wait();
                } catch (InterruptedException ignored) {
                }
            }
            this.working = true;
            this.lock.notifyAll();
        }
    }

    public void clearAllUpdates() {
        synchronized (this.lock) {
            unpauseToEndOfUpdate();
            this.updateList.clear();
            this.lock.notifyAll();
        }
    }

    public int getPendingUpdatesCount() {
        synchronized (this.lock) {
            int count = this.updateList.size();
            if (this.currentBuilder != null)
                count++;
            return count;
        }
    }

    public int resetUpdateCount() {
        synchronized (this.lock) {
            int count = this.updateCount;
            this.updateCount = 0;
            return count;
        }
    }

    public void yieldWork() {
        TessellatorAccessor.smoothbeta_setInstance(mainTessellator);
        UpdateThread.this.checkCanWork();
        TessellatorAccessor.smoothbeta_setInstance(threadTessellator);
    }
}
