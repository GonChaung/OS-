def print_header(algo_name):
    header = f"{algo_name} PAGE REPLACEMENT ALGORITHM"
    print(f"\n\t{header}")
    print(f"\t{'=' * len(header)}\n")


def get_optimal_victim(frames, ref_list, current_idx):
    farthest_idx = -1
    victim_idx = -1

    for i in range(len(frames)):
        page = frames[i]

        try:
            next_use = ref_list[current_idx + 1:].index(page)
        except ValueError:
            return i

        if next_use > farthest_idx:
            farthest_idx = next_use
            victim_idx = i

    return victim_idx


def get_lru_victim(frames, ref_list, current_idx):
    lru_time = current_idx
    victim_idx = -1

    for i, page in enumerate(frames):
        last_used = -1

        for j in range(current_idx - 1, -1, -1):
            if ref_list[j] == page:
                last_used = j
                break

        if last_used < lru_time:
            lru_time = last_used
            victim_idx = i

    return victim_idx


def print_teacher_diagram(initial_frames, replacement_records):
    """
    Teacher-style sparse diagram.

    First column:
        shows the initial pages loaded into memory.

    After that:
        only the frame that changes during each page replacement is shown.
    """

    if initial_frames is None:
        return

    print("Page Allocation:")
    print("----------------")

    cell_width = 7

    for frame_index in range(len(initial_frames)):

        line = ""

        # Initial frame contents
        value = initial_frames[frame_index]
        line += f"[{value}]".ljust(cell_width)

        # For every page replacement, print ONLY the changed frame
        for record in replacement_records:
            victim_index, old_page, new_page = record

            if victim_index == frame_index:
                line += f"[{new_page}]".ljust(cell_width)
            else:
                line += " ".ljust(cell_width)

        print(line.rstrip())

    print()


def run_simulation(ref_string, size, algo_option):
    ref_list = list(ref_string)
    frames = [None] * size

    if algo_option == 1:
        algo_name = "OPTIMAL"
    elif algo_option == 2:
        algo_name = "LRU"
    else:
        algo_name = "FIFO"

    print_header(algo_name)

    fifo_ptr = 0
    replacement_records = []
    initial_frames = None

    for i, page in enumerate(ref_list):

        # Page already exists in memory
        if page in frames:
            continue

        # Empty frame available -> initial allocation only
        if None in frames:
            empty_index = frames.index(None)
            frames[empty_index] = page
            continue

        # Save the initial full-frame state before the first replacement
        if initial_frames is None:
            initial_frames = frames.copy()

        # Select victim
        if algo_option == 1:
            victim_index = get_optimal_victim(
                frames, ref_list, i
            )

        elif algo_option == 2:
            victim_index = get_lru_victim(
                frames, ref_list, i
            )

        else:
            victim_index = fifo_ptr
            fifo_ptr = (fifo_ptr + 1) % size

        old_page = frames[victim_index]

        # Replace victim page
        frames[victim_index] = page

        # Save:
        # frame index, removed page, incoming page
        replacement_records.append(
            (victim_index, old_page, page)
        )

    # If there was no replacement, still show loaded frames
    if initial_frames is None:
        initial_frames = frames.copy()

    print(f"Page Reference String: {ref_string}")
    print(f"Memory Frames       : {size}\n")

    # Teacher-style sparse frame diagram
    print_teacher_diagram(
        initial_frames,
        replacement_records
    )

    # Teacher-style PF notation
    print("Page Fault Resolutions:")
    print("-----------------------")

    if len(replacement_records) == 0:
        print("No page replacement is required.\n")
        print(
            f"{algo_name}: No PFs resolved with page replacement."
        )
        return

    for victim_index, old_page, new_page in replacement_records:
        print(f"({old_page} <- {new_page}) PF")

    print()

    answer = ", ".join(
        f"({old_page} <- {new_page}) PF"
        for victim_index, old_page, new_page
        in replacement_records
    )

    print(f"{algo_name}: {answer}")
    print(
        f"Total {len(replacement_records)} PFs resolved "
        f"with page replacement."
    )


def get_user_inputs():
    try:
        r_str = input(
            "Enter a Page reference string without space <Max 20>: "
        ).strip()

        f_size = int(
            input("Enter the size of Memory frame <Max 10>: ")
        )

        return r_str, f_size

    except ValueError:
        print("Invalid input format.")
        return None, None


def main():
    print("\n\n\tPAGE REPLACEMENT ALGORITHM SIMULATION")
    print("\t=====================================\n")

    ref_string, frame_size = get_user_inputs()

    if ref_string is None:
        return

    while True:
        print("\n\tAlgorithm Option Menu:")
        print("\t======================")
        print("\t1. OPTIMAL  2. LRU")
        print("\t3. FIFO     4. Exit\n")

        choice = input(
            "Select an OPTION from above by entering its number: "
        ).strip()

        if choice == "4":
            break

        if choice in ["1", "2", "3"]:

            run_simulation(
                ref_string,
                frame_size,
                int(choice)
            )

            cont = input(
                "\nEnter Y to continue or press any other key to Exit: "
            ).strip().lower()

            if cont != "y":
                break

            reuse = input(
                "Enter N to use the same Page Reference and "
                "Memory Frame or press any key: "
            ).strip().lower()

            if reuse != "n":
                print(
                    "\n\tPAGE REPLACEMENT ALGORITHM SIMULATION"
                )
                print(
                    "\t=====================================\n"
                )

                ref_string, frame_size = get_user_inputs()

                if ref_string is None:
                    break

        else:
            print("Invalid Option. Please select 1-4.")


if __name__ == "__main__":
    main()
